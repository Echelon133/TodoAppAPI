package ml.echelon133.Service;

import ml.echelon133.Model.Authority;
import ml.echelon133.Repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityService implements IAuthorityService {

    private AuthorityRepository authorityRepository;

    @Autowired
    public AuthorityService(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public Authority getAuthorityByAuthority(String authorityName) {
        return authorityRepository.getAuthorityByAuthority(authorityName);
    }
}
