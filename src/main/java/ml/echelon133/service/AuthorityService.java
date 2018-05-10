package ml.echelon133.service;

import ml.echelon133.model.Authority;
import ml.echelon133.repository.AuthorityRepository;
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
