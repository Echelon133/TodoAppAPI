package ml.echelon133.Service;

import ml.echelon133.Model.User;
import ml.echelon133.Repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;


@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private BCryptPasswordEncoder realPasswordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        realPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    public void savingNewUserEncodesUserPassword() throws Exception {
        String password = "password";

        // Prepare a new User with null Id
        User user = new User("username", password);

        // Given
        given(passwordEncoder.encode(password)).willCallRealMethod();
        given(userRepository.save(user)).willReturn(user);

        // When
        User savedUser = userService.save(user);
        String encodedPassword = savedUser.getPassword();

        // Then
        assertThat(realPasswordEncoder.matches(password, encodedPassword)).isTrue();
    }

    @Test
    public void savingExistingUserDoesNotEncodePasswordAgain() throws Exception {
        String password = "password";

        // Prepare a User with Id not null
        String passwordBeforeSave = realPasswordEncoder.encode(password);
        User user = new User("username", passwordBeforeSave);
        user.setId(1L);

        // Given
        given(userRepository.save(user)).willReturn(user);

        // When
        User savedUser = userService.save(user);
        String passwordAfterSave = savedUser.getPassword();

        // Then
        assertThat(passwordAfterSave.equals(passwordBeforeSave)).isTrue();
    }
}
