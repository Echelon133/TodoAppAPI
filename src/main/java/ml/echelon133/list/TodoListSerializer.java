package ml.echelon133.list;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ml.echelon133.task.Task;

import java.io.IOException;
import java.util.Set;

public class TodoListSerializer extends JsonSerializer<TodoList> {

    @Override
    public void serialize(TodoList todoList, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        JsonSerializer<Object> taskSerializer = serializerProvider.findValueSerializer(Task.class);

        Set<Task> tasks = todoList.getTasks();

        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", todoList.getId());
        jsonGenerator.writeStringField("dateCreated", todoList.getDateCreated().toString());
        jsonGenerator.writeStringField("name", todoList.getName());
        jsonGenerator.writeArrayFieldStart("tasks");
        for (Task task : tasks) {
            taskSerializer.serialize(task, jsonGenerator, serializerProvider);
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
