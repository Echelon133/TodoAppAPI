package ml.echelon133.Service;

import ml.echelon133.Model.User;
import ml.echelon133.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

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
