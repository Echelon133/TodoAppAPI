package ml.echelon133.Model.Validator;

import ml.echelon133.Model.DTO.NewUserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, Object> {

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        NewUserDTO newUserObj = (NewUserDTO) o;
        Boolean isValid = false;
        try {
            isValid = newUserObj.getPassword().equals(newUserObj.getPasswordConfirm());
        } catch (NullPointerException ex) {
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void initialize(PasswordsMatch constraintAnnotation) {
    }
}
