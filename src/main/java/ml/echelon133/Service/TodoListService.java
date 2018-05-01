package ml.echelon133.Service;

import ml.echelon133.Model.TodoList;
import ml.echelon133.Repository.TodoListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoListService implements ITodoListService {

    private TodoListRepository todoListRepository;

    @Autowired
    public TodoListService(TodoListRepository todoListRepository) {
        this.todoListRepository = todoListRepository;
    }

    @Override
    public List<TodoList> getAllByListOwner_Username(String username) {
        return todoListRepository.getAllByListOwner_Username(username);
    }

    @Override
    public TodoList getByIdAndUsername(Long id, String username) {
        return todoListRepository.getByIdAndListOwner_Username(id, username);
    }

    @Override
    public TodoList save(TodoList todoList) {
        return todoListRepository.save(todoList);
    }

    @Override
    public Boolean delete(TodoList todoList) {
        Boolean wasDeleted;
        Long id = todoList.getId();

        todoListRepository.delete(todoList);
        wasDeleted = !todoListRepository.existsById(id);
        return wasDeleted;
    }
}
