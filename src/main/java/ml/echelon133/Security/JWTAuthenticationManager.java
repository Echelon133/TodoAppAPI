package ml.echelon133.Security;

import ml.echelon133.Model.User;
import ml.echelon133.Service.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class JWTAuthenticationManager implements AuthenticationManager {

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JWTToken jwtToken = (JWTToken)authentication;
        String token = jwtToken.getToken();
        String username = tokenService.extractUsernameFromToken(token);
        if (username == null) {
            throw new BadCredentialsException("Malformed token");
        }
        if (!tokenService.isValidToken(token)) {
            throw new BadCredentialsException("Token expired or invalid");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                token,
                userDetails.getAuthorities()
        );
        return auth;
    }
}
