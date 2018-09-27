package ml.echelon133.token;

public interface ITokenRepository {
    void setTokenOfUser(String username, String token, Integer hoursTimeToLive);
    String getTokenOfUser(String username);
}
