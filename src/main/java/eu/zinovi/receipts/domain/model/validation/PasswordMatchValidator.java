package eu.zinovi.receipts.domain.model.validation;

import eu.zinovi.receipts.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, String> {
private final UserService userService;

public PasswordMatchValidator(UserService userService) {
        this.userService = userService;
        }

@Override
public boolean isValid(String value, ConstraintValidatorContext context) {
        return userService.checkPassword(value);
        }
}
