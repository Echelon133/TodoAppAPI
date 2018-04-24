package ml.echelon133.Model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ml.echelon133.Model.Serializer.TaskSerializer;

import javax.persistence.*;
import java.util.Date;

@Entity
@JsonSerialize(using = TaskSerializer.class)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date dateCreated;
    private String taskContent;
    private Boolean isFinished;

    @ManyToOne
    @JoinColumn(name="task_owner")
    private TodoList taskOwner;

    public Task() {
        setDateCreated(new Date());
        setFinished(false);
    }
    public Task(String taskContent) {
        this();
        setTaskContent(taskContent);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public void setFinished(Boolean finished) {
        isFinished = finished;
    }

    public TodoList getTaskOwner() {
        return taskOwner;
    }

    public void setTaskOwner(TodoList taskOwner) {
        this.taskOwner = taskOwner;
    }
}
