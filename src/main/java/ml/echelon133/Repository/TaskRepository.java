package ml.echelon133.Repository;

import ml.echelon133.Model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Return All Tasks that belong to a TodoList with specified id and username
    // This way there is no chance that the user will receive tasks that don't belong to him
    @Query("Select t from Task t, User u, TodoList tl" +
            "    where t.taskOwner=tl.id" +
            "          and tl.listOwner=u.id" +
            "          and tl.id=:todoListId" +
            "          and u.username=:username")
    Set<Task> getAllTasksByTodoListIdAndUsername(@Param("todoListId") Long todoListId,
                                                 @Param("username") String username);

    @Query("Select t from Task t, User u, TodoList tl" +
            "    where t.taskOwner.id=:listId" +
            "          and tl.listOwner=u.id" +
            "          and t.id=:taskId" +
            "          and u.username=:username")
    Task getTaskByListIdAndTaskIdAndUsername(@Param("listId") Long listId,
                                    @Param("taskId") Long taskId,
                                    @Param("username") String username);
}
