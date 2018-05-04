package ml.echelon133.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ml.echelon133.Model.DTO.APIMessage;
import ml.echelon133.Model.DTO.TaskDTO;
import ml.echelon133.Model.Task;
import ml.echelon133.Model.TodoList;
import ml.echelon133.Service.TaskService;
import ml.echelon133.Service.TodoListService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskControllerTest {

    private MockMvc mvc;

    @Mock
    private Principal principal;

    @Mock
    private WebApplicationContext context;

    @Mock
    private TodoListService todoListService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private APIExceptionHandler exceptionHandler;

    @InjectMocks
    private TaskController taskController;

    private JacksonTester<Task> jsonTask;

    private JacksonTester<Set<Task>> jsonTasks;

    private JacksonTester<TaskDTO> jsonTaskDTO;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());

        mvc = MockMvcBuilders.standaloneSetup(taskController).setControllerAdvice(exceptionHandler).build();

        given(context.getBean("apiMessage")).willReturn(new APIMessage());
    }

    @Test
    public void getEmptyTasks() throws Exception {
        // Prepare empty task set
        Set<Task> tasks = new HashSet<>();

        // Given
        given(principal.getName()).willReturn("test_user");
        given(taskService.getAllTasksByTodoListIdAndUsername(1L, "test_user")).willReturn(tasks);

        // When
        MockHttpServletResponse response = mvc.perform(
                get("/api/todo-lists/1/tasks")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[]");
    }

    @Test
    public void getAllTasksFromNotEmptySet() throws Exception {
        // Prepare not empty task set
        Set<Task> tasks = new HashSet<>();
        Task firstTask = new Task("first task name");
        Task secondTask = new Task("second task name");
        firstTask.setId(1L);
        secondTask.setId(2L);
        tasks.add(firstTask);
        tasks.add(secondTask);

        // Prepare expected json response
        JsonContent<Set<Task>> tasksJsonContent = jsonTasks.write(tasks);

        // Given
        given(principal.getName()).willReturn("test_user");
        given(taskService.getAllTasksByTodoListIdAndUsername(1L, "test_user")).willReturn(tasks);

        // When
        MockHttpServletResponse response = mvc.perform(
                get("/api/todo-lists/1/tasks")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(tasksJsonContent.getJson());
    }

    @Test
    public void taskCannotBeAddedToTodoListWhenTaskContentIsNull() throws Exception {
        // Prepare TaskDTO
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskContent(null);
        taskDTO.setFinished(false);

        // Prepare TaskDTO json
        JsonContent<TaskDTO> taskDTOJsonContent = jsonTaskDTO.write(taskDTO);

        // Given
        given(principal.getName()).willReturn("test_user");

        // When
        MockHttpServletResponse response = mvc.perform(
                post("/api/todo-lists/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskDTOJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON).principal(principal))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("taskContent must not be null");
    }

    @Test
    public void taskCannotBeAddedToTodoListWhenTaskContentLengthIsInvalid() throws Exception {
        // Prepare TaskDTO
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskContent("aa");
        taskDTO.setFinished(false);

        // Prepare TaskDTO json
        JsonContent<TaskDTO> taskDTOJsonContent = jsonTaskDTO.write(taskDTO);

        // Given
        given(principal.getName()).willReturn("test_user");

        // When
        MockHttpServletResponse response = mvc.perform(
                post("/api/todo-lists/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskDTOJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON).principal(principal))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("taskContent length must be between 5 and 100");
    }

    @Test
    public void taskCannotBeAddedToTodoListWhenTodoListDoesNotExist() throws Exception {
        // Prepare TaskDTO
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskContent("valid task content");
        taskDTO.setFinished(false);

        // Prepare TaskDTO json
        JsonContent<TaskDTO> taskDTOJsonContent = jsonTaskDTO.write(taskDTO);

        // Given
        given(principal.getName()).willReturn("test_user");
        given(todoListService.getByIdAndUsername(1L, "test_user")).willReturn(null);

        // When
        MockHttpServletResponse response = mvc.perform(
                post("/api/todo-lists/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskDTOJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON).principal(principal))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("List does not exists. Cannot add a task");
    }

    @Test
    public void taskCanBeAddedToTodoListWhenItsValidAndTodoListExists() throws Exception {
        // Prepare TaskDTO
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskContent("valid task content");
        taskDTO.setFinished(false);

        // Prepare TaskDTO json
        JsonContent<TaskDTO> taskDTOJsonContent = jsonTaskDTO.write(taskDTO);

        // Prepare TodoList
        TodoList todoList = new TodoList();
        todoList.setId(1L);
        todoList.setName("Example TodoList");

        // Given
        given(principal.getName()).willReturn("test_user");
        given(todoListService.getByIdAndUsername(1L, "test_user")).willReturn(todoList);

        // When
        MockHttpServletResponse response = mvc.perform(
                post("/api/todo-lists/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskDTOJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON).principal(principal))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).contains("Task created successfully");
    }

    @Test
    public void specificTaskCannotBeDisplayedWhenItDoesNotExist() throws Exception {
        // Given
        given(principal.getName()).willReturn("test_user");
        given(taskService.getTaskByListIdAndTaskIdAndUsername(1L, 1L, "test_user")).willReturn(null);

        // When
        MockHttpServletResponse response = mvc.perform(
                get("/api/todo-lists/1/tasks/1")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("Task does not exist");
    }

    @Test
    public void specificTaskCanBeDisplayedWhenItExists() throws Exception {
        // Prepare Task
        Task task = new Task();
        task.setId(1L);
        task.setTaskContent("Task content");

        // Prepare expected Task Json
        JsonContent<Task> taskJsonContent = jsonTask.write(task);

        // Given
        given(principal.getName()).willReturn("test_user");
        given(taskService.getTaskByListIdAndTaskIdAndUsername(1L, 1L, "test_user")).willReturn(task);

        // When
        MockHttpServletResponse response = mvc.perform(
                get("/api/todo-lists/1/tasks/1")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(taskJsonContent.getJson());
    }

    @Test
    public void taskContentCannotBeChangedToNull() throws Exception {
        // Prepare TaskDTO
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskContent(null);
        taskDTO.setFinished(false);

        // Prepare TaskDTO json
        JsonContent<TaskDTO> taskDTOJsonContent = jsonTaskDTO.write(taskDTO);

        // Given
        given(principal.getName()).willReturn("test_user");

        // When
        MockHttpServletResponse response = mvc.perform(
                put("/api/todo-lists/1/tasks/1")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskDTOJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("taskContent must not be null");
    }

    @Test
    public void taskContentCannotBeChangedToContentWithInvalidLength() throws Exception {
        // Prepare TaskDTO
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskContent("aa");
        taskDTO.setFinished(false);

        // Prepare TaskDTO json
        JsonContent<TaskDTO> taskDTOJsonContent = jsonTaskDTO.write(taskDTO);

        // Given
        given(principal.getName()).willReturn("test_user");

        // When
        MockHttpServletResponse response = mvc.perform(
                put("/api/todo-lists/1/tasks/1")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskDTOJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("taskContent length must be between 5 and 100");
    }

    @Test
    public void taskContentCannotBeChangedWhenTaskDoesNotExist() throws Exception {
        // Prepare TaskDTO
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskContent("valid task content");
        taskDTO.setFinished(false);

        // Prepare TaskDTO json
        JsonContent<TaskDTO> taskDTOJsonContent = jsonTaskDTO.write(taskDTO);

        // Given
        given(principal.getName()).willReturn("test_user");
        given(taskService.getTaskByListIdAndTaskIdAndUsername(1L, 1L, "test_user")).willReturn(null);

        // When
        MockHttpServletResponse response = mvc.perform(
                put("/api/todo-lists/1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskDTOJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON).principal(principal))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("Task does not exist");
    }

    @Test
    public void taskContentCanBeChangedWhenTaskIsValid() throws Exception {
        // Prepare TaskDTO
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskContent("valid task content");
        taskDTO.setFinished(false);

        // Prepare TaskDTO json
        JsonContent<TaskDTO> taskDTOJsonContent = jsonTaskDTO.write(taskDTO);

        // Prepare Task
        Task task = new Task();
        task.setId(1L);
        task.setTaskContent("Task content");

        // Given
        given(principal.getName()).willReturn("test_user");
        given(taskService.getTaskByListIdAndTaskIdAndUsername(1L, 1L, "test_user")).willReturn(task);

        // When
        MockHttpServletResponse response = mvc.perform(
                put("/api/todo-lists/1/tasks/1")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskDTOJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("Task updated successfully");
    }

    @Test
    public void taskCannotBeDeletedWhenTaskDoesNotExist() throws Exception {
        // Given
        given(principal.getName()).willReturn("test_user");
        given(taskService.getTaskByListIdAndTaskIdAndUsername(1L, 1L, "test_user")).willReturn(null);

        // When
        MockHttpServletResponse response = mvc.perform(
                delete("/api/todo-lists/1/tasks/1")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("Task does not exist");
    }

    @Test
    public void taskCanBeDeletedWhenTaskExists() throws Exception {
        // Prepare Task
        Task task = new Task();
        task.setId(1L);
        task.setTaskContent("Task content");

        // Given
        given(principal.getName()).willReturn("test_user");
        given(taskService.getTaskByListIdAndTaskIdAndUsername(1L, 1L, "test_user")).willReturn(task);
        given(taskService.delete(task)).willReturn(true);

        // When
        MockHttpServletResponse response = mvc.perform(
                delete("/api/todo-lists/1/tasks/1")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("Task deleted successfully");
    }

    @Test
    public void taskCannotBeDeletedWhenServiceFails() throws Exception {
        // Prepare Task
        Task task = new Task();
        task.setId(1L);
        task.setTaskContent("Task content");

        // Given
        given(principal.getName()).willReturn("test_user");
        given(taskService.getTaskByListIdAndTaskIdAndUsername(1L, 1L, "test_user")).willReturn(task);
        given(taskService.delete(task)).willReturn(false);

        // When
        MockHttpServletResponse response = mvc.perform(
                delete("/api/todo-lists/1/tasks/1")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getContentAsString()).contains("Failed to delete task");
    }
}
