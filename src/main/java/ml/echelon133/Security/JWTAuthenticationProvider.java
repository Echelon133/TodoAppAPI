package ml.echelon133.Security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class JWTAuthenticationProvider implements AuthenticationProvider {

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationProvider(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return authenticationManager.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return JWTToken.class.isAssignableFrom(aClass);
    }
}
