package ml.echelon133.token;

public interface ITokenService {
    String getTokenOfUser(String username);
    void setTokenOfUser(String username, String token, Integer hoursTimeToLive);
    String generateTokenForUser(String username);
    String extractUsernameFromToken(String token);
    Boolean isValidToken(String token);
}
