package ml.echelon133.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import ml.echelon133.Repository.TokenRepository;
import ml.echelon133.Repository.UserRepository;
import org.junit.Before;
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
}
