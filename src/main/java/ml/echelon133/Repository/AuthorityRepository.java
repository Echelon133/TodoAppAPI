package ml.echelon133.Repository;

import ml.echelon133.Model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority getAuthorityByAuthority(String authority);
}
