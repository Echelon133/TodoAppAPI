package ml.echelon133.list;

import com.fasterxml.jackson.databind.ObjectMapper;
import ml.echelon133.exception.APIExceptionHandler;
import ml.echelon133.list.TodoListController;
import ml.echelon133.message.APIMessage;
import ml.echelon133.list.TodoListDTO;
import ml.echelon133.list.TodoList;
import ml.echelon133.user.User;
import ml.echelon133.list.TodoListService;
import ml.echelon133.user.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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

    private JacksonTester<TodoList> jsonTodoList;

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

    @Test
    public void todoListCannotBeSavedWhenNameLengthIsInvalid() throws Exception {
        // Prepare TodoListDTO
        TodoListDTO todoListDTO = new TodoListDTO();
        todoListDTO.setName("asd");

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
        assertThat(response.getContentAsString()).contains("name length must be between 4 and 60");
    }

    @Test
    public void todoListCanBeSavedWhenNameIsValid() throws Exception {
        // Prepare TodoListDTO
        TodoListDTO todoListDTO = new TodoListDTO();
        todoListDTO.setName("Valid name of a list");

        // Prepare TodoListDTO json
        JsonContent<TodoListDTO> todoListDTOJsonContent = jsonTodoListDTO.write(todoListDTO);

        // Given
        given(principal.getName()).willReturn("test_user");
        given(userService.getUserByUsername("test_user")).willReturn(new User("test_user", "test_password"));

        // When
        MockHttpServletResponse response = mvc.perform(
                post("/api/todo-lists")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoListDTOJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).contains("Successful list creation");
    }

    @Test
    public void getSpecificTodoListReturnsNotFoundWhenListDoesNotExist() throws Exception {
        // Given
        given(principal.getName()).willReturn("test_user");
        given(todoListService.getByIdAndUsername(1L, "test_user")).willReturn(null);

        // When
        MockHttpServletResponse response = mvc.perform(
                get("/api/todo-lists/1")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("List not found");
    }

    @Test
    public void getSpecificTodoListReturnsTodoListIfItExists() throws Exception {
        // Prepare TodoList
        TodoList todoList = new TodoList();
        todoList.setId(1L);
        todoList.setName("TodoList name");

        // Prepare expected json
        JsonContent<TodoList> todoListJsonContent = jsonTodoList.write(todoList);

        // Given
        given(principal.getName()).willReturn("test_user");
        given(todoListService.getByIdAndUsername(1L, "test_user")).willReturn(todoList);

        // When
        MockHttpServletResponse response = mvc.perform(
                get("/api/todo-lists/1")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(todoListJsonContent.getJson());
    }

    @Test
    public void todoListNameCannotBeChangedToNull() throws Exception {
        // Prepare TodoListDTO
        TodoListDTO todoListDTO = new TodoListDTO();
        todoListDTO.setName(null);

        // Prepare TodoListDTO json
        JsonContent<TodoListDTO> todoListDTOJsonContent = jsonTodoListDTO.write(todoListDTO);

        // Given
        given(principal.getName()).willReturn("test_user");

        // When
        MockHttpServletResponse response = mvc.perform(
                put("/api/todo-lists/1")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoListDTOJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("name must not be null");
    }

    @Test
    public void todoListNameCannotBeChangedWhenNewNameLengthIsInvalid() throws Exception {
        // Prepare TodoListDTO
        TodoListDTO todoListDTO = new TodoListDTO();
        todoListDTO.setName("New");

        // Prepare TodoListDTO json
        JsonContent<TodoListDTO> todoListDTOJsonContent = jsonTodoListDTO.write(todoListDTO);

        // Given
        given(principal.getName()).willReturn("test_user");

        // When
        MockHttpServletResponse response = mvc.perform(
                put("/api/todo-lists/1")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoListDTOJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("name length must be between 4 and 60");
    }

    @Test
    public void todoListNameCannotBeChangedWhenTodoListIsNotFound() throws Exception {
        // Prepare TodoListDTO
        TodoListDTO todoListDTO = new TodoListDTO();
        todoListDTO.setName("Valid name");

        // Prepare TodoListDTO json
        JsonContent<TodoListDTO> todoListDTOJsonContent = jsonTodoListDTO.write(todoListDTO);

        // Given
        given(principal.getName()).willReturn("test_user");
        given(todoListService.getByIdAndUsername(1L, "test_user")).willReturn(null);

        // When
        MockHttpServletResponse response = mvc.perform(
                put("/api/todo-lists/1")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoListDTOJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("List not found");
    }

    @Test
    public void todoListNameCanBeChangedWhenNewNameIsValid() throws Exception {
        // Prepare TodoListDTO
        TodoListDTO todoListDTO = new TodoListDTO();
        todoListDTO.setName("Valid name");

        // Prepare TodoList
        TodoList todoList = new TodoList();
        todoList.setId(1L);
        todoList.setName("TodoList name");

        // Prepare TodoListDTO json
        JsonContent<TodoListDTO> todoListDTOJsonContent = jsonTodoListDTO.write(todoListDTO);

        // Given
        given(principal.getName()).willReturn("test_user");
        given(todoListService.getByIdAndUsername(1L, "test_user")).willReturn(todoList);

        // When
        MockHttpServletResponse response = mvc.perform(
                put("/api/todo-lists/1")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoListDTOJsonContent.getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("List name changed successfully");
    }

    @Test
    public void todoListCannotBeDeletedWhenListDoesNotExist() throws Exception {
        // Given
        given(principal.getName()).willReturn("test_user");
        given(todoListService.getByIdAndUsername(1L, "test_user")).willReturn(null);

        // When
        MockHttpServletResponse response = mvc.perform(
                delete("/api/todo-lists/1")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("List not found");
    }

    @Test
    public void todoListCanBeDeletedWhenListExists() throws Exception {
        // Prepare TodoList
        TodoList todoList = new TodoList();
        todoList.setId(1L);
        todoList.setName("TodoList name");

        // Given
        given(principal.getName()).willReturn("test_user");
        given(todoListService.getByIdAndUsername(1L, "test_user")).willReturn(todoList);
        given(todoListService.delete(todoList)).willReturn(true);

        // When
        MockHttpServletResponse response = mvc.perform(
                delete("/api/todo-lists/1")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("List deleted successfully");
    }

    @Test
    public void todoListCannotBeDeletedWhenServiceFails() throws Exception {
        // Prepare TodoList
        TodoList todoList = new TodoList();
        todoList.setId(1L);
        todoList.setName("TodoList name");

        // Given
        given(principal.getName()).willReturn("test_user");
        given(todoListService.getByIdAndUsername(1L, "test_user")).willReturn(todoList);
        given(todoListService.delete(todoList)).willReturn(false);

        // When
        MockHttpServletResponse response = mvc.perform(
                delete("/api/todo-lists/1")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getContentAsString()).contains("Failed to delete list");
    }
}
