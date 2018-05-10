package ml.echelon133.repository;

import ml.echelon133.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    User getUserByUsername(String username);

    @Query("Select u.secret from User u where u.username=?1")
    String getSecretOfUser(@Param("username") String username);
}
