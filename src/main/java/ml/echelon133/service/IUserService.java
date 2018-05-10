package ml.echelon133.service;

import ml.echelon133.model.User;

public interface IUserService {
    User save(User user);
    User getUserByUsername(String username);
    String getSecretOfUser(String username);
}
