package ml.echelon133.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ml.echelon133.Model.DTO.APIMessage;
import ml.echelon133.Model.DTO.TaskDTO;
import ml.echelon133.Model.Task;
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
}
