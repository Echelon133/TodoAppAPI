package ml.echelon133.Service;

import ml.echelon133.Model.Authority;
import ml.echelon133.Repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthorityService implements IAuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public Authority getAuthorityByAuthorityName(String authorityName) {
        return authorityRepository.getAuthorityByAuthorityName(authorityName);
    }
}
