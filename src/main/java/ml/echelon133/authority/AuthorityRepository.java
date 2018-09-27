package ml.echelon133.authority;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority getAuthorityByAuthority(String authority);
}
