package ml.echelon133.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import ml.echelon133.Repository.ITokenRepository;
import ml.echelon133.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Service
public class TokenService implements ITokenService {

    @Autowired
    private ITokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String getTokenOfUser(String username) {
        return tokenRepository.getTokenOfUser(username);
    }

    @Override
    public void setTokenOfUser(String username, String token, Integer hoursTimeToLive) {
        tokenRepository.setTokenOfUser(username, token, hoursTimeToLive);
    }

    @Override
    public String generateTokenForUser(String username) {
        String secret = userRepository.getSecretOfUser(username);
        Algorithm algorithm;
        String token;

        try {
            algorithm = Algorithm.HMAC512(secret);
            token = JWT
                    .create()
                    .withClaim("date", new Date())
                    .withClaim("username", username)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException ex) {
            token = null;
        }
        return token;
    }

    @Override
    public String extractUsernameFromToken(String token) {
        String extractedUsername;
        String tokenWithoutPrefix = token.substring(7); // Token without 'Bearer ' prefix
        DecodedJWT jwt;

        try {
            jwt = JWT.decode(tokenWithoutPrefix);
            extractedUsername = jwt.getClaim("username").asString();
        } catch (JWTDecodeException ex) {
            extractedUsername = null;
        }
        return extractedUsername;
    }

    @Override
    public Boolean isValidToken(String token) {
        Boolean isValid;
        String username = extractUsernameFromToken(token);
        String redisStoredToken = getTokenOfUser(username);
        String fullToken = "Bearer " + redisStoredToken;

        if (fullToken.equals(token)) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }
}
