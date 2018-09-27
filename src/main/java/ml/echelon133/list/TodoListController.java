package ml.echelon133.controller;

import ml.echelon133.exception.ObjectFailedValidationException;
import ml.echelon133.exception.ResourceDoesNotExistException;
import ml.echelon133.model.dto.IAPIMessage;
import ml.echelon133.model.dto.TodoListDTO;
import ml.echelon133.model.TodoList;
import ml.echelon133.model.User;
import ml.echelon133.service.ITodoListService;
import ml.echelon133.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class TodoListController {

    private WebApplicationContext context;
    private ITodoListService todoListService;
    private IUserService userService;

    @Autowired
    public TodoListController(WebApplicationContext context, ITodoListService todoListService, IUserService userService) {
        this.context = context;
        this.todoListService = todoListService;
        this.userService = userService;
    }

    public IAPIMessage getApiMessage() {
        return (IAPIMessage)context.getBean("apiMessage");
    }

    @RequestMapping(value="/api/todo-lists", method = RequestMethod.GET)
    public List<TodoList> getAllLists(Principal principal) {
        String username = principal.getName();
        List<TodoList> allLists = todoListService.getAllByListOwner_Username(username);
        return allLists;
    }

    @RequestMapping(value="/api/todo-lists", method = RequestMethod.POST)
    public ResponseEntity<IAPIMessage> addNewList(Principal principal,
                                                  @Valid @RequestBody TodoListDTO todoListDTO,
                                                  BindingResult result) throws ObjectFailedValidationException {
        String username = principal.getName();

        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            throw new ObjectFailedValidationException(fieldErrors);
        }

        User user = userService.getUserByUsername(username);

        TodoList todoList = new TodoList();
        todoList.setName(todoListDTO.getName());
        user.addTodoList(todoList);
        userService.save(user);

        IAPIMessage apiMessage = getApiMessage();
        apiMessage.setHttpStatus(HttpStatus.CREATED);
        apiMessage.addMessage("Successful list creation");
        return new ResponseEntity<>(apiMessage, apiMessage.getHttpStatus());
    }

    @RequestMapping(value="/api/todo-lists/{listId}", method = RequestMethod.GET)
    public TodoList getSpecificTodoList(Principal principal, @PathVariable("listId") Long id) throws ResourceDoesNotExistException {
        String username = principal.getName();
        TodoList todoList = todoListService.getByIdAndUsername(id, username);
        if (todoList == null) {
            // list with specified id might exist, but it does not belong to the user
            throw new ResourceDoesNotExistException("List not found");
        }
        return todoList;
    }

    @RequestMapping(value="/api/todo-lists/{listId}", method = RequestMethod.PUT)
    public ResponseEntity<IAPIMessage> changeNameOfTodoList(Principal principal,
                                         @PathVariable("listId") Long id,
                                         @Valid @RequestBody TodoListDTO todoListDTO,
                                         BindingResult result) throws ResourceDoesNotExistException, ObjectFailedValidationException {
        String username = principal.getName();

        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            throw new ObjectFailedValidationException(fieldErrors);
        }

        TodoList todoList = todoListService.getByIdAndUsername(id, username);

        if (todoList == null) {
            throw new ResourceDoesNotExistException("List not found");
        } else {
            String newName = todoListDTO.getName();
            todoList.setName(newName);
            todoListService.save(todoList);
        }

        IAPIMessage apiMessage = getApiMessage();
        apiMessage.setHttpStatus(HttpStatus.OK);
        apiMessage.addMessage("List name changed successfully");
        return new ResponseEntity<IAPIMessage>(apiMessage, apiMessage.getHttpStatus());
    }

    @RequestMapping(value="/api/todo-lists/{listId}", method = RequestMethod.DELETE)
    public ResponseEntity<IAPIMessage> deleteTodoList(Principal principal, @PathVariable("listId") Long id) throws ResourceDoesNotExistException {
        String username = principal.getName();
        Boolean wasDeleted;

        TodoList todoList = todoListService.getByIdAndUsername(id, username);
        if (todoList == null) {
            // list does not exist or user does not own it, so deletion shouldn't be possible
            throw new ResourceDoesNotExistException("List not found");
        }
        wasDeleted = todoListService.delete(todoList);

        IAPIMessage apiMessage = getApiMessage();

        if (wasDeleted) {
            apiMessage.setHttpStatus(HttpStatus.OK);
            apiMessage.addMessage("List deleted successfully");
        } else {
            // delete is called only if the resource exists and user has rights to delete it
            // if resource was not deleted, it means that something went wrong and it is not user fault
            apiMessage.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            apiMessage.addMessage("Failed to delete list");
        }
        return new ResponseEntity<IAPIMessage>(apiMessage, apiMessage.getHttpStatus());
    }
}
