package ml.echelon133.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class TokenRepository implements ITokenRepository {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void setTokenOfUser(String username, String token, Integer hoursTimeToLive) {
        BoundValueOperations<String, String> ops = redisTemplate.boundValueOps(username);
        ops.set(token, hoursTimeToLive, TimeUnit.HOURS);
    }

    @Override
    public String getTokenOfUser(String username) {
        BoundValueOperations<String, String> ops = redisTemplate.boundValueOps(username);
        String token;
        try {
            token = ops.get();
        } catch (NullPointerException ex) {
            token = "";
        }
        return token;
    }
}
