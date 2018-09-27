package ml.echelon133.list;

import ml.echelon133.list.TodoList;

import java.util.List;

public interface ITodoListService {
    List<TodoList> getAllByListOwner_Username(String username);
    TodoList getByIdAndUsername(Long id, String username);
    TodoList save(TodoList todoList);
    Boolean delete(TodoList todoList);
}
