package ml.echelon133.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class TokenController {

    @RequestMapping(value="/users/token", method= RequestMethod.POST)
    public String getToken(Principal principal) {
        return "TEST_TOKEN";
    }
}
