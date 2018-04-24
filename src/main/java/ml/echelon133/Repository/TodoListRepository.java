package ml.echelon133.Repository;

import ml.echelon133.Model.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoListRepository extends JpaRepository<TodoList, Long> {
    List<TodoList> getAllByListOwner_Username(String username);
}
