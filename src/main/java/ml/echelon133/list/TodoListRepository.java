package ml.echelon133.repository;

import ml.echelon133.model.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoListRepository extends JpaRepository<TodoList, Long> {
    List<TodoList> getAllByListOwner_Username(String username);
    TodoList getByIdAndListOwner_Username(Long id, String username);
}
