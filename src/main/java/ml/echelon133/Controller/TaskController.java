package ml.echelon133.Controller;

import ml.echelon133.Exception.ObjectFailedValidationException;
import ml.echelon133.Exception.ResourceDoesNotExistException;
import ml.echelon133.Model.DTO.IAPIMessage;
import ml.echelon133.Model.DTO.TaskDTO;
import ml.echelon133.Model.Task;
import ml.echelon133.Model.TodoList;
import ml.echelon133.Service.ITaskService;
import ml.echelon133.Service.ITodoListService;
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
import java.util.Set;

@RestController
public class TaskController {

    private ITodoListService todoListService;
    private ITaskService taskService;
    private WebApplicationContext context;

    @Autowired
    public TaskController(ITodoListService todoListService, ITaskService taskService, WebApplicationContext context) {
        this.todoListService = todoListService;
        this.taskService = taskService;
        this.context = context;
    }

    public IAPIMessage getApiMessage() {
        return (IAPIMessage)context.getBean("apiMessage");
    }

    @RequestMapping(value = "/api/todo-lists/{listId}/tasks", method = RequestMethod.GET)
    public Set<Task> getAllTasksFromSpecificList(Principal principal, @PathVariable("listId") Long listId) {
        String username = principal.getName();
        Set<Task> tasks = taskService.getAllTasksByTodoListIdAndUsername(listId, username);
        return tasks;
    }

    @RequestMapping(value = "/api/todo-lists/{listId}/tasks", method = RequestMethod.POST)
    public ResponseEntity<IAPIMessage> addTaskToList(Principal principal,
                                                     @PathVariable("listId") Long listId,
                                                     @Valid @RequestBody TaskDTO taskDTO,
                                                     BindingResult result) throws ObjectFailedValidationException, ResourceDoesNotExistException {

        String username = principal.getName();
        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            throw new ObjectFailedValidationException(fieldErrors);
        }

        TodoList todoList = todoListService.getByIdAndUsername(listId, username);
        if (todoList == null) {
            throw new ResourceDoesNotExistException("List does not exists. Cannot add a task");
        }

        Task newTask = new Task();
        newTask.setTaskContent(taskDTO.getTaskContent());
        newTask.setFinished(taskDTO.getFinished());

        todoList.addTask(newTask);
        todoListService.save(todoList);

        IAPIMessage apiMessage = getApiMessage();
        apiMessage.setHttpStatus(HttpStatus.CREATED);
        apiMessage.addMessage("Task created successfully");
        return new ResponseEntity<>(apiMessage, apiMessage.getHttpStatus());
    }

    @RequestMapping(value = "/api/todo-lists/{listId}/tasks/{taskId}", method = RequestMethod.GET)
    public Task getSpecificTask(Principal principal,
                                @PathVariable("listId") Long listId,
                                @PathVariable("taskId") Long taskId) throws ResourceDoesNotExistException{
        String username = principal.getName();
        Task task = taskService.getTaskByListIdAndTaskIdAndUsername(listId, taskId, username);

        if (task == null) {
            throw new ResourceDoesNotExistException("Task does not exist");
        }
        return task;
    }

    @RequestMapping(value = "/api/todo-lists/{listId}/tasks/{taskId}", method = RequestMethod.PUT)
    public ResponseEntity<IAPIMessage> updateTask(Principal principal,
                                 @PathVariable("listId") Long listId,
                                 @PathVariable("taskId") Long taskId,
                                 @Valid @RequestBody TaskDTO taskDTO,
                                 BindingResult result) throws ResourceDoesNotExistException, ObjectFailedValidationException {

        String username = principal.getName();

        if (result.hasErrors()) {
            List<FieldError> errors = result.getFieldErrors();
            throw new ObjectFailedValidationException(errors);
        }

        Task task = taskService.getTaskByListIdAndTaskIdAndUsername(listId, taskId, username);

        if (task == null) {
            throw new ResourceDoesNotExistException("Task does not exist");
        }

        task.setTaskContent(taskDTO.getTaskContent());
        task.setFinished(taskDTO.getFinished());
        taskService.save(task);

        IAPIMessage apiMessage = getApiMessage();
        apiMessage.setHttpStatus(HttpStatus.OK);
        apiMessage.addMessage("Task updated successfuly");
        return new ResponseEntity<>(apiMessage, apiMessage.getHttpStatus());
    }

    @RequestMapping(value = "/api/todo-lists/{listId}/tasks/{taskId}", method = RequestMethod.DELETE)
    public ResponseEntity<IAPIMessage> deleteTask(Principal principal,
                                 @PathVariable("listId") Long listId,
                                 @PathVariable("taskId") Long taskId) throws ResourceDoesNotExistException {
        String username = principal.getName();
        Boolean wasDeleted;

        Task task = taskService.getTaskByListIdAndTaskIdAndUsername(listId, taskId, username);
        if (task == null) {
            throw new ResourceDoesNotExistException("Task does not exist");
        }

        wasDeleted = taskService.delete(task);

        IAPIMessage apiMessage = getApiMessage();
        if (wasDeleted) {
            apiMessage.setHttpStatus(HttpStatus.OK);
            apiMessage.addMessage("Task deleted successfully");
        } else {
            apiMessage.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            apiMessage.addMessage("Failed to delete task");
        }
        return new ResponseEntity<>(apiMessage, apiMessage.getHttpStatus());
    }
}
