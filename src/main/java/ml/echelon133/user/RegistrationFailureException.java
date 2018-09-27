package ml.echelon133.user;

import java.util.List;

public class RegistrationFailureException extends Exception {

    private List<String> errors;

    public RegistrationFailureException(String msg) {
        super(msg);
    }

    public RegistrationFailureException(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
