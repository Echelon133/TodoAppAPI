package ml.echelon133.Controller;

import ml.echelon133.Exception.ObjectFailedValidationException;
import ml.echelon133.Exception.ResourceDoesNotExistException;
import ml.echelon133.Model.DTO.APIMessage;
import ml.echelon133.Model.DTO.TaskDTO;
import ml.echelon133.Model.Task;
import ml.echelon133.Model.TodoList;
import ml.echelon133.Service.ITaskService;
import ml.echelon133.Service.ITodoListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
public class TaskController {

    @Autowired
    private ITodoListService todoListService;

    @Autowired
    private ITaskService taskService;

    @RequestMapping(value = "/api/todo-lists/{listId}/tasks", method = RequestMethod.GET)
    public Set<Task> getAllTasksFromSpecificList(Principal principal, @PathVariable("listId") Long listId) {
        String username = principal.getName();
        Set<Task> tasks = taskService.getAllTasksByTodoListIdAndUsername(listId, username);
        return tasks;
    }

    @RequestMapping(value = "/api/todo-lists/{listId}/tasks", method = RequestMethod.POST)
    public APIMessage addTaskToList(Principal principal,
                                    @PathVariable("listId") Long listId,
                                    @Valid @RequestBody TaskDTO taskDTO,
                                    BindingResult result) throws ObjectFailedValidationException, ResourceDoesNotExistException {

        String username = principal.getName();
        if (result.hasErrors()) {
            System.out.println(taskDTO.getTaskContent());
            System.out.println(taskDTO.getFinished());
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

        APIMessage apiMessage = new APIMessage(HttpStatus.CREATED);
        apiMessage.addMessage("Task created successfully");
        return apiMessage;
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
    public APIMessage updateTask(Principal principal,
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

        APIMessage apiMessage = new APIMessage(HttpStatus.OK);
        apiMessage.addMessage("Task updated successfuly");
        return apiMessage;
    }
}
