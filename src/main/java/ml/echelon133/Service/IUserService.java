package ml.echelon133.Service;

import ml.echelon133.Model.User;

public interface IUserService {
    User save(User user);
    User getUserByUsername(String username);
    String getSecretOfUser(String username);
}
