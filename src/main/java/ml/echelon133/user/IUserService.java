package ml.echelon133.user;

import ml.echelon133.user.User;

public interface IUserService {
    User save(User user);
    User getUserByUsername(String username);
    String getSecretOfUser(String username);
}
