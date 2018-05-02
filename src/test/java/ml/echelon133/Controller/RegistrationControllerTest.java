package ml.echelon133.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ml.echelon133.Exception.RegistrationFailureException;
import ml.echelon133.Model.DTO.APIMessage;
import ml.echelon133.Model.DTO.IAPIMessage;
import ml.echelon133.Model.DTO.NewUserDTO;
import ml.echelon133.Repository.AuthorityRepository;
import ml.echelon133.Repository.UserRepository;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationControllerTest {

    private MockMvc mvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private WebApplicationContext context;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private AuthorityService authorityService;

    @InjectMocks
    private APIExceptionHandler exceptionHandler;

    @InjectMocks
    private RegistrationController registrationController;

    private JacksonTester<IAPIMessage> jsonApiMessage;

    private JacksonTester<NewUserDTO> jsonNewUser;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());

        mvc = MockMvcBuilders.standaloneSetup(registrationController).setControllerAdvice(exceptionHandler).build();
    }

    @Test
    public void userCannotBeRegisteredWhenPasswordsNotEqual() throws Exception {
        // Prepare NewUser json in which passwords do not match
        NewUserDTO newUserDTO = new NewUserDTO("test_user", "first_password", "first_password1");
        JsonContent<NewUserDTO> newUserJson = jsonNewUser.write(newUserDTO);

        // APIExceptionHandler uses APIMessage objects in which it returns the response
        // In the real application APIMessage objects are request scoped and taken from WebApplicationContext
        // Here WebApplicationContext getBean call is fixed to return APIMessage because request scope
        // is not needed since APIMessage is called only once
        given(context.getBean("apiMessage")).willReturn(new APIMessage());

        // When sent JSON has passwords that do not match
        MockHttpServletResponse response = mvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson.getJson())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse();

        // Then returned status is 400 and response contains text message with error
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Passwords do not match");
    }
}
