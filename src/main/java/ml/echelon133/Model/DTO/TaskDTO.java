package ml.echelon133.Model.DTO;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class TaskDTO {

    @NotNull
    @Length(min=5, max=100)
    private String taskContent;

    @NotNull
    private boolean isFinished;

    public TaskDTO() {}

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }
}
