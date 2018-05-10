package ml.echelon133.controller;

import ml.echelon133.service.TokenService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(MockitoJUnitRunner.class)
public class TokenControllerTest {

    private MockMvc mvc;

    @Mock
    private TokenService tokenService;

    @Mock
    private Principal principal;

    @InjectMocks
    private TokenController tokenController;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(tokenController).build();
    }

    @Test
    public void getTokenThatIsAlreadyStoredInDatabase() throws Exception {
        String token = "test.token.content";

        // Given
        given(principal.getName()).willReturn("test_user");
        given(tokenService.getTokenOfUser("test_user")).willReturn(token);

        // When
        MockHttpServletResponse response = mvc.perform(
                post("/users/token").principal(principal))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains(token);
    }

    @Test
    public void getTokenGeneratedOnTheSpot() throws Exception {
        String token = "new.token.content";

        // Given
        given(principal.getName()).willReturn("test_user");
        given(tokenService.getTokenOfUser("test_user")).willReturn(null);
        given(tokenService.generateTokenForUser("test_user")).willReturn(token);

        // When
        MockHttpServletResponse response = mvc.perform(
                post("/users/token").principal(principal))
                .andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains(token);
    }
}
