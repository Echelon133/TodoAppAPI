package ml.echelon133.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ml.echelon133.Model.Authority;
import ml.echelon133.Model.DTO.APIMessage;
import ml.echelon133.Model.DTO.IAPIMessage;
import ml.echelon133.Model.DTO.NewUserDTO;
import ml.echelon133.Model.User;
import ml.echelon133.Service.AuthorityService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationControllerTest {

    private MockMvc mvc;

    @Mock
    private WebApplicationContext context;

    @Mock
    private UserService userService;

    @Mock
    private AuthorityService authorityService;

    @InjectMocks
    private APIExceptionHandler exceptionHandler;

    @InjectMocks
    private RegistrationController registrationController;

    private JacksonTester<NewUserDTO> jsonNewUser;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());

        // APIExceptionHandler uses APIMessage objects in which it returns the response
        // In the real application APIMessage objects are request scoped and taken from WebApplicationContext
        // Here WebApplicationContext getBean call is fixed to return APIMessage because request scope
        // is not needed since APIMessage is called only once
        given(context.getBean("apiMessage")).willReturn(new APIMessage());

        mvc = MockMvcBuilders.standaloneSetup(registrationController).setControllerAdvice(exceptionHandler).build();
    }

    @Test
    public void userCannotBeRegisteredWhenPasswordsNotEqual() throws Exception {
        // Prepare NewUserDTO json in which passwords do not match
        NewUserDTO newUserDTO = new NewUserDTO("test_user", "first_password", "first_password1");
        JsonContent<NewUserDTO> newUserJson = jsonNewUser.write(newUserDTO);

        // When sent JSON has passwords that do not match
        MockHttpServletResponse response = mvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson.getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then returned status is 400 and response contains text message with error
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Passwords do not match");
    }

    @Test
    public void userCannotBeRegisteredWhenUsernameLengthIsInvalid() throws Exception {
        // Prepare NewUserDTO json in which username is too short
        NewUserDTO newUserDTO = new NewUserDTO("tt", "valid_password", "valid_password");
        JsonContent<NewUserDTO> newUserJson = jsonNewUser.write(newUserDTO);

        // When sent JSON has username that is too short
        MockHttpServletResponse response = mvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson.getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then returned status is 400 and response contains text message with error
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("username length must be between 6 and 25");
    }

    @Test
    public void userCannotBeRegisteredWhenUsernameIsAlreadyTaken() throws Exception {
        // Prepare NewUserDTO
        NewUserDTO newUserDTO = new NewUserDTO("my_username", "valid_password", "valid_password");
        JsonContent<NewUserDTO> newUserJson = jsonNewUser.write(newUserDTO);

        // Given username return user object that is not null
        given(userService.getUserByUsername("my_username")).willReturn(new User("my_username", "test_password"));

        // When sent JSON with username that is already taken
        MockHttpServletResponse response = mvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson.getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then returned status is 409 and response contains text message with error
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getContentAsString()).contains("User with that username already exists");
    }

    @Test
    public void userCanBeCreatedProperlyWithValidInput() throws Exception {
        // Prepare NewUserDTO with valid data
        NewUserDTO newUserDTO = new NewUserDTO("test_username", "test_password", "test_password");
        JsonContent<NewUserDTO> newUserJson = jsonNewUser.write(newUserDTO);

        // Given
        given(userService.getUserByUsername("test_username")).willReturn(null);
        given(authorityService.getAuthorityByAuthority("ROLE_USER")).willReturn(new Authority("ROLE_USER"));
        given(userService.save(any(User.class))).willReturn(null);

        // When sent JSON with username that is not taken and passwords match
        MockHttpServletResponse response = mvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson.getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Then returned status is 201 and response contains text message with success text
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).contains("Registration successful");
    }
}
