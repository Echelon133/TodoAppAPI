package ml.echelon133.controller;

import ml.echelon133.service.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class TokenController {

    private ITokenService tokenService;

    @Autowired
    public TokenController(ITokenService tokenService) {
        this.tokenService = tokenService;
    }

    @RequestMapping(value="/users/token", method= RequestMethod.POST)
    public String getToken(Principal principal) {
        String username = principal.getName();

        String token = tokenService.getTokenOfUser(username);

        if (token == null) {
            // create a new token and push it to redis
            token = tokenService.generateTokenForUser(username);
            tokenService.setTokenOfUser(username, token, 5);
        }
        return token;
    }
}
