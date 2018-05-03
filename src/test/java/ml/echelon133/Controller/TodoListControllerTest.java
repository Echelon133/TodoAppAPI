package ml.echelon133.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ml.echelon133.Model.DTO.APIMessage;
import ml.echelon133.Model.DTO.TodoListDTO;
import ml.echelon133.Model.TodoList;
import ml.echelon133.Service.TodoListService;
import ml.echelon133.Service.UserService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(MockitoJUnitRunner.class)
public class TodoListControllerTest {

    private MockMvc mvc;

    @Mock
    private Principal principal;

    @Mock
    private WebApplicationContext context;

    @Mock
    private TodoListService todoListService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TodoListController todoListController;

    @InjectMocks
    private APIExceptionHandler exceptionHandler;

    private JacksonTester<List<TodoList>> jsonTodoLists;

    private JacksonTester<TodoListDTO> jsonTodoListDTO;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());

        mvc = MockMvcBuilders.standaloneSetup(todoListController).setControllerAdvice(exceptionHandler).build();

        given(context.getBean("apiMessage")).willReturn(new APIMessage());
    }

    @Test
    public void getEmptyTodoLists() throws Exception {
        List<TodoList> todoLists = new ArrayList<>();

        // Given
        given(principal.getName()).willReturn("test_user");
        given(todoListService.getAllByListOwner_Username("test_user")).willReturn(todoLists);

        // When
        MockHttpServletResponse response = mvc.perform(
                get("/api/todo-lists")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[]");
    }

    @Test
    public void getAllTodoLists() throws Exception {
        // Prepare todoLists
        TodoList todoList = new TodoList();
        todoList.setId(1L);
        todoList.setName("Example list");
        List<TodoList> todoLists = Arrays.asList(todoList);

        // Prepare expected json
        JsonContent<List<TodoList>> todoListsJsonContent = jsonTodoLists.write(todoLists);

        // Given
        given(principal.getName()).willReturn("test_user");
        given(todoListService.getAllByListOwner_Username("test_user")).willReturn(todoLists);

        // When
        MockHttpServletResponse response = mvc.perform(
                get("/api/todo-lists")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(todoListsJsonContent.getJson());
    }

    @Test
    public void todoListCannotBeSavedWhenNameIsNull() throws Exception {
        // Prepare TodoListDTO
        TodoListDTO todoListDTO = new TodoListDTO();
        todoListDTO.setName(null);

        // Prepare TodoListDTO json
        JsonContent<TodoListDTO> todoListDTOJsonContent = jsonTodoListDTO.write(todoListDTO);

        // Given
        given(principal.getName()).willReturn("test_user");

        // When
        MockHttpServletResponse response = mvc.perform(
                post("/api/todo-lists")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoListDTOJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("name must not be null");
    }
}
