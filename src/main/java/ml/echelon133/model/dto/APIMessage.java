package ml.echelon133.model.dto;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class APIMessage implements IAPIMessage {
    private HttpStatus httpStatus;
    private List<String> messages;

    public APIMessage() {
        setHttpStatus(HttpStatus.OK);
        setMessages(new ArrayList<String>());
    }

    public APIMessage(HttpStatus httpStatus) {
        setHttpStatus(httpStatus);
        setMessages(new ArrayList<String>());
    }

    public APIMessage(HttpStatus httpStatus, List<String> messages) {
        setHttpStatus(httpStatus);
        setMessages(messages);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }
}
