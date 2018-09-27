package ml.echelon133.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import ml.echelon133.token.TokenRepository;
import ml.echelon133.user.UserRepository;
import ml.echelon133.token.TokenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TokenService tokenService;

    @Test
    public void generateTokenForUserGeneratesTokenWithValidClaims() throws Exception {
        String testUsername = "test_user";
        String testSecret = "aaaaaaaaaaaaaaaa";
        Date date = new Date();

        // Given
        given(userRepository.getSecretOfUser(testUsername)).willReturn(testSecret);

        // When
        String generatedToken = tokenService.generateTokenForUser(testUsername);

        // Decode token elements
        DecodedJWT decodedJWT = JWT.decode(generatedToken);
        String decodedUsername = decodedJWT.getClaim("username").asString();
        Date decodedDate = decodedJWT.getClaim("date").asDate();

        // Then
        assertThat(generatedToken).matches("^(.+)\\.(.+)\\.(.+)$");
        assertThat(decodedUsername).isEqualTo(testUsername);
        assertThat(decodedDate).isEqualToIgnoringSeconds(date);
    }

    @Test
    public void extractUsernameFromTokenExtractsUsernameFromValidToken() throws Exception {
        String testUsername = "test_user";
        String testSecret = "aaaaaaaaaaaaaaaa";
        Date date = new Date();
        StringBuilder tokenBuilder = new StringBuilder("Bearer ");

        // Prepare test token
        Algorithm algorithm = Algorithm.HMAC512(testSecret);
        String token = JWT.create().withClaim("date", date).withClaim("username", testUsername).sign(algorithm);
        tokenBuilder.append(token);

        // When
        String extractedUsername = tokenService.extractUsernameFromToken(tokenBuilder.toString());

        // Then
        assertThat(extractedUsername).isEqualTo(testUsername);
    }

    @Test
    public void isValidTokenValidatesCorrectToken() throws Exception {
        String testUsername = "test_user";
        String testSecret = "aaaaaaaaaaaaaaaa";
        StringBuilder tokenBuilder = new StringBuilder("Bearer ");

        // Prepare test token
        Algorithm algorithm = Algorithm.HMAC512(testSecret);
        String token = JWT.create().withClaim("date", new Date()).withClaim("username", testUsername).sign(algorithm);
        tokenBuilder.append(token);

        // Given
        given(tokenRepository.getTokenOfUser(testUsername)).willReturn(token);

        // When
        Boolean isValid = tokenService.isValidToken(tokenBuilder.toString());

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    public void isValidTokenDoesNotValidateIncorrectToken() throws Exception {
        String testUsername = "test_user";
        Algorithm algorithm;

        // Prepare valid token
        String validSecret = "aaaaaaaaaaaaaaaa";
        algorithm = Algorithm.HMAC512(validSecret);
        String validToken = JWT.create().withClaim("date", new Date()).withClaim("username", testUsername).sign(algorithm);

        // Prepare 'invalid' token
        String invalidSecret = "bbbbbbbbbbbbbbbb";
        algorithm = Algorithm.HMAC512(invalidSecret);
        String invalidToken = JWT.create().withClaim("date", new Date()).withClaim("username", testUsername).sign(algorithm);
        StringBuilder tokenBuilder = new StringBuilder("Bearer ");
        tokenBuilder.append(invalidToken);

        // Given
        given(tokenRepository.getTokenOfUser(testUsername)).willReturn(validToken);

        // When
        Boolean isValid = tokenService.isValidToken(tokenBuilder.toString());

        // Then
        assertThat(isValid).isFalse();
    }
}
