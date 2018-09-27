package ml.echelon133.message;

import org.springframework.http.HttpStatus;

import java.util.List;

public interface IAPIMessage {
    HttpStatus getHttpStatus();
    void setHttpStatus(HttpStatus httpStatus);
    List<String> getMessages();
    void setMessages(List<String> messages);
    void addMessage(String message);
}
