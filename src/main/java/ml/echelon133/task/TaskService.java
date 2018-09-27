package ml.echelon133.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TaskService implements ITaskService {

    private TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Set<Task> getAllTasksByTodoListIdAndUsername(Long todoListId, String username) {
        return taskRepository.getAllTasksByTodoListIdAndUsername(todoListId, username);
    }

    @Override
    public Task getTaskByListIdAndTaskIdAndUsername(Long listId, Long taskId, String username) {
        return taskRepository.getTaskByListIdAndTaskIdAndUsername(listId, taskId, username);
    }

    @Override
    public Boolean delete(Task task) {
        Boolean wasDeleted;
        Long id = task.getId();

        taskRepository.delete(task);
        taskRepository.existsById(id);
        wasDeleted = !taskRepository.existsById(id);
        return wasDeleted;
    }

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }
}
