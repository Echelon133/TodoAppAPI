package ml.echelon133.Controller;

import ml.echelon133.Exception.RegistrationFailureException;
import ml.echelon133.Exception.ResourceDoesNotExistException;
import ml.echelon133.Exception.TodoListFailedValidationException;
import ml.echelon133.Exception.UsernameAlreadyTakenException;
import ml.echelon133.Model.DTO.APIMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        APIMessage apiMessage = new APIMessage(HttpStatus.BAD_REQUEST);
        apiMessage.addMessage(ex.getMessage());
        return new ResponseEntity<>(apiMessage, apiMessage.getHttpStatus());
    }

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    protected ResponseEntity<Object> handleUsernameAlreadyTakenException(UsernameAlreadyTakenException ex) {
        APIMessage apiMessage = new APIMessage(HttpStatus.CONFLICT);
        apiMessage.addMessage(ex.getMessage());
        return new ResponseEntity<>(apiMessage, apiMessage.getHttpStatus());
    }

    @ExceptionHandler(RegistrationFailureException.class)
    protected ResponseEntity<Object> handleRegistrationFailureException(RegistrationFailureException ex) {
        APIMessage apiMessage = new APIMessage(HttpStatus.BAD_REQUEST);
        apiMessage.setMessages(ex.getErrors());
        return new ResponseEntity<>(apiMessage, apiMessage.getHttpStatus());
    }

    @ExceptionHandler(TodoListFailedValidationException.class)
    protected ResponseEntity<Object> handleTodoListFailedValidationException(TodoListFailedValidationException ex) {
        APIMessage apiMessage = new APIMessage(HttpStatus.BAD_REQUEST);
        apiMessage.setMessages(ex.getTextErrors());
        return new ResponseEntity<>(apiMessage, apiMessage.getHttpStatus());
    }

    @ExceptionHandler(ResourceDoesNotExistException.class)
    protected ResponseEntity<Object> handleResourceDoesNotExistException(ResourceDoesNotExistException ex) {
        APIMessage apiMessage = new APIMessage(HttpStatus.NOT_FOUND);
        apiMessage.addMessage(ex.getMessage());
        return new ResponseEntity<>(apiMessage, apiMessage.getHttpStatus());
    }
}
