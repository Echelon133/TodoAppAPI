package ml.echelon133.Controller;

import ml.echelon133.Exception.ObjectFailedValidationException;
import ml.echelon133.Exception.RegistrationFailureException;
import ml.echelon133.Exception.ResourceDoesNotExistException;
import ml.echelon133.Exception.UsernameAlreadyTakenException;
import ml.echelon133.Model.DTO.IAPIMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private WebApplicationContext context;

    public IAPIMessage getApiMessage() {
        return (IAPIMessage)context.getBean("apiMessage");
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        IAPIMessage apiMessage = getApiMessage();
        apiMessage.setHttpStatus(HttpStatus.BAD_REQUEST);
        apiMessage.addMessage(ex.getMessage());
        return new ResponseEntity<>(apiMessage, apiMessage.getHttpStatus());
    }

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    protected ResponseEntity<IAPIMessage> handleUsernameAlreadyTakenException(UsernameAlreadyTakenException ex) {
        IAPIMessage apiMessage = getApiMessage();
        apiMessage.setHttpStatus(HttpStatus.CONFLICT);
        apiMessage.addMessage(ex.getMessage());
        return new ResponseEntity<>(apiMessage, apiMessage.getHttpStatus());
    }

    @ExceptionHandler(RegistrationFailureException.class)
    protected ResponseEntity<IAPIMessage> handleRegistrationFailureException(RegistrationFailureException ex) {
        IAPIMessage apiMessage = getApiMessage();
        apiMessage.setHttpStatus(HttpStatus.BAD_REQUEST);
        apiMessage.setMessages(ex.getErrors());
        return new ResponseEntity<>(apiMessage, apiMessage.getHttpStatus());
    }

    @ExceptionHandler(ObjectFailedValidationException.class)
    protected ResponseEntity<IAPIMessage> handleTodoListFailedValidationException(ObjectFailedValidationException ex) {
        IAPIMessage apiMessage = getApiMessage();
        apiMessage.setHttpStatus(HttpStatus.BAD_REQUEST);
        apiMessage.setMessages(ex.getTextErrors());
        return new ResponseEntity<>(apiMessage, apiMessage.getHttpStatus());
    }

    @ExceptionHandler(ResourceDoesNotExistException.class)
    protected ResponseEntity<IAPIMessage> handleResourceDoesNotExistException(ResourceDoesNotExistException ex) {
        IAPIMessage apiMessage = getApiMessage();
        apiMessage.setHttpStatus(HttpStatus.NOT_FOUND);
        apiMessage.addMessage(ex.getMessage());
        return new ResponseEntity<>(apiMessage, apiMessage.getHttpStatus());
    }
}
