package eu.zinovi.receipts.domain.model.validation;

import eu.zinovi.receipts.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailExistsValidator implements ConstraintValidator<EmailExists, String> {
    private final UserService userService;

    public EmailExistsValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return userService.emailExists(value);
    }
}
