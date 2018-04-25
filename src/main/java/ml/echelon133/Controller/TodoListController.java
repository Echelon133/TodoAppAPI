package ml.echelon133.Controller;

import com.sun.xml.internal.bind.v2.TODO;
import ml.echelon133.Exception.ResourceDoesNotExistException;
import ml.echelon133.Exception.TodoListFailedValidationException;
import ml.echelon133.Model.DTO.APIMessage;
import ml.echelon133.Model.DTO.NewListDTO;
import ml.echelon133.Model.TodoList;
import ml.echelon133.Model.User;
import ml.echelon133.Service.ITodoListService;
import ml.echelon133.Service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TodoListController {

    @Autowired
    private ITodoListService todoListService;

    @Autowired
    private IUserService userService;

    @RequestMapping(value="/api/todo-lists", method = RequestMethod.GET)
    public List<TodoList> getAllLists(Principal principal) {
        String username = principal.getName();
        List<TodoList> allLists = todoListService.getAllByListOwner_Username(username);
        return allLists;
    }

    @RequestMapping(value="/api/todo-lists", method = RequestMethod.POST)
    public APIMessage addNewList(Principal principal,
                                 @Valid @RequestBody NewListDTO newListDTO,
                                 BindingResult result) throws TodoListFailedValidationException {
        String username = principal.getName();

        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            List<String> textErrors = new ArrayList<>();

            for (FieldError fError : fieldErrors) {
                textErrors.add(fError.getField() + " " + fError.getDefaultMessage());
            }
            throw new TodoListFailedValidationException(textErrors);
        }

        User user = userService.getUserByUsername(username);

        TodoList todoList = new TodoList();
        todoList.setName(newListDTO.getName());
        user.addTodoList(todoList);
        userService.save(user);

        APIMessage apiMessage = new APIMessage(HttpStatus.CREATED);
        apiMessage.addMessage("Successful list creation");
        return apiMessage;
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
}
