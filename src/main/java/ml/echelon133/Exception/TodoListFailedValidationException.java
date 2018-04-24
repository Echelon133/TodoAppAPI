package ml.echelon133.Exception;

import java.util.List;

public class TodoListFailedValidationException extends Exception {

    private List<String> errors;

    public TodoListFailedValidationException(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
