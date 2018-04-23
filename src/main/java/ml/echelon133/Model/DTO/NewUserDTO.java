package ml.echelon133.Model.DTO;

import ml.echelon133.Model.Validator.PasswordsMatch;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@PasswordsMatch
public class NewUserDTO {


    @NotEmpty
    @Length(min=6, max=25)
    private String username;

    @NotEmpty
    @NotNull
    @Length(min=6)
    private String password;

    @NotNull
    private String passwordConfirm;

    public NewUserDTO() {}
    public NewUserDTO(String username, String password, String passwordConfirm) {
        setUsername(username);
        setPassword(password);
        setPasswordConfirm(passwordConfirm);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
