package ml.echelon133.task;

import java.util.Set;

public interface ITaskService {
    Set<Task> getAllTasksByTodoListIdAndUsername(Long todoListId, String username);
    Task getTaskByListIdAndTaskIdAndUsername(Long listId, Long taskId, String username);
    Boolean delete(Task task);
    Task save(Task task);
}
