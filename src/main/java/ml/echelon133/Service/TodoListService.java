package ml.echelon133.Service;

import ml.echelon133.Model.TodoList;
import ml.echelon133.Repository.TodoListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoListService implements ITodoListService {

    @Autowired
    private TodoListRepository todoListRepository;

    @Override
    public List<TodoList> getAllByListOwner_Username(String username) {
        return todoListRepository.getAllByListOwner_Username(username);
    }

    @Override
    public TodoList save(TodoList todoList) {
        return todoListRepository.save(todoList);
    }
}
