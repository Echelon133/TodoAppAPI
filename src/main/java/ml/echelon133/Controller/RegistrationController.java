package ml.echelon133.Controller;

import ml.echelon133.Exception.RegistrationFailureException;
import ml.echelon133.Exception.UsernameAlreadyTakenException;
import ml.echelon133.Model.Authority;
import ml.echelon133.Model.DTO.APIMessage;
import ml.echelon133.Model.DTO.NewUserDTO;
import ml.echelon133.Model.User;
import ml.echelon133.Service.IAuthorityService;
import ml.echelon133.Service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class RegistrationController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IAuthorityService authorityService;

    @RequestMapping(value="/users/register", method= RequestMethod.POST)
    public APIMessage registerUser(@Valid @RequestBody NewUserDTO newUserDTO, BindingResult result)
            throws UsernameAlreadyTakenException, RegistrationFailureException {
        APIMessage apiMessage;

        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            String errorsChained = fieldErrors.toString();
            throw new RegistrationFailureException(errorsChained);
        }

        String username = newUserDTO.getUsername();
        String password = newUserDTO.getPassword();

        if (userService.getUserByUsername(username) != null) {
            throw new UsernameAlreadyTakenException("User with that username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        Authority authority = authorityService.getAuthorityByAuthority("ROLE_USER");
        if (authority == null) {
            authority = new Authority("ROLE_USER");
            user.addAuthority(authority);
        }

        userService.save(user);
        apiMessage = new APIMessage(HttpStatus.CREATED, "Registration successful");
        return apiMessage;
    }

}
