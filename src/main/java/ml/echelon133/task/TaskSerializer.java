package ml.echelon133.task;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ml.echelon133.task.Task;

import java.io.IOException;

public class TaskSerializer extends JsonSerializer<Task> {

    @Override
    public void serialize(Task task, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", task.getId());
        jsonGenerator.writeStringField("dateCreated", task.getDateCreated().toString());
        jsonGenerator.writeStringField("taskContent", task.getTaskContent());
        jsonGenerator.writeBooleanField("finished", task.getFinished());
        jsonGenerator.writeEndObject();
    }
}
