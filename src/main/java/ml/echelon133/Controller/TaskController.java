package ml.echelon133.Controller;

import ml.echelon133.Model.Task;
import ml.echelon133.Service.ITaskService;
import ml.echelon133.Service.ITodoListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
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
}
