package ml.echelon133.controller;

import ml.echelon133.exception.RegistrationFailureException;
import ml.echelon133.exception.UsernameAlreadyTakenException;
import ml.echelon133.model.Authority;
import ml.echelon133.model.dto.IAPIMessage;
import ml.echelon133.model.dto.NewUserDTO;
import ml.echelon133.model.User;
import ml.echelon133.service.IAuthorityService;
import ml.echelon133.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RegistrationController {

    private WebApplicationContext context;
    private IUserService userService;
    private IAuthorityService authorityService;

    @Autowired
    public RegistrationController(WebApplicationContext context, IUserService userService, IAuthorityService authorityService) {
        this.context = context;
        this.userService = userService;
        this.authorityService = authorityService;
    }

    public IAPIMessage getApiMessage() {
        return (IAPIMessage)context.getBean("apiMessage");
    }

    @RequestMapping(value="/users/register", method= RequestMethod.POST)
    public ResponseEntity<IAPIMessage> registerUser(@Valid @RequestBody NewUserDTO newUserDTO, BindingResult result)
            throws UsernameAlreadyTakenException, RegistrationFailureException {

        if (result.hasErrors()) {
            ObjectError oError = result.getGlobalError();
            List<FieldError> fErrors = result.getFieldErrors();
            List<String> textErrors = new ArrayList<>();

            if (oError != null) {
                textErrors.add(oError.getDefaultMessage());
            }

            for (FieldError fError : fErrors) {
                textErrors.add(fError.getField() + " " + fError.getDefaultMessage());
            }
            throw new RegistrationFailureException(textErrors);
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
        }
        user.addAuthority(authority);

        userService.save(user);

        IAPIMessage apiMessage = getApiMessage();
        apiMessage.setHttpStatus(HttpStatus.CREATED);
        apiMessage.addMessage("Registration successful");
        return new ResponseEntity<>(apiMessage, apiMessage.getHttpStatus());
    }

}
