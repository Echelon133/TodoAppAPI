package ml.echelon133.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            // if ID is null it means that we are creating a new user, so we need to encode the plaintext password
            String plainTextPassword = user.getPassword();
            String encodedPassword = passwordEncoder.encode(plainTextPassword);
            user.setPassword(encodedPassword);
        }
        return userRepository.save(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    @Override
    public String getSecretOfUser(String username) {
        return userRepository.getSecretOfUser(username);
    }
}
