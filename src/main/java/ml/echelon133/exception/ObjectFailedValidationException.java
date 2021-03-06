package ml.echelon133.exception;

import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

public class ObjectFailedValidationException extends Exception {

    private List<FieldError> errors;

    public ObjectFailedValidationException(List<FieldError> errors) {
        this.errors = errors;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldError> errors) {
        this.errors = errors;
    }

    public List<String> getTextErrors() {
        List<String> textErrors = new ArrayList<>();
        for (FieldError fError : this.errors) {
            textErrors.add(fError.getField() + " " + fError.getDefaultMessage());
        }
        return textErrors;
    }
}
