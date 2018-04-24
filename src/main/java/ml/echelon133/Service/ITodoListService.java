package ml.echelon133.Service;

import ml.echelon133.Model.TodoList;

import java.util.List;

public interface ITodoListService {
    List<TodoList> getAllByListOwner_Username(String username);
    TodoList save(TodoList todoList);
}
