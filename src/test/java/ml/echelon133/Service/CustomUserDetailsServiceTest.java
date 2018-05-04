package ml.echelon133.Service;

import ml.echelon133.Model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Before
    public void setup() {}

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsernameThrowsExceptionWhenUserIsNull() throws Exception {
        // Given
        given(userService.getUserByUsername("test_user")).willReturn(null);

        // When
        userDetailsService.loadUserByUsername("test_user");
    }

    @Test
    public void loadUserByUsernameLoadsCorrectUserFromUserService() throws Exception {
        // Prepare user
        User user = new User("test_user", "password");

        // Given
        given(userService.getUserByUsername("test_user")).willReturn(user);

        // When
        UserDetails returnedUser = userDetailsService.loadUserByUsername("test_user");

        // Then
        assertThat(returnedUser.equals(user));
    }

}
