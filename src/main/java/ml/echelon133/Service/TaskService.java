package ml.echelon133.Service;

import ml.echelon133.Model.Task;
import ml.echelon133.Repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TaskService implements ITaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Set<Task> getAllTasksByTodoListIdAndUsername(Long todoListId, String username) {
        return taskRepository.getAllTasksByTodoListIdAndUsername(todoListId, username);
    }

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }
}
