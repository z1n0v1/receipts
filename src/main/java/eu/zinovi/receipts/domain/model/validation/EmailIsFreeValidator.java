package eu.zinovi.receipts.domain.model.validation;

import eu.zinovi.receipts.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailIsFreeValidator implements ConstraintValidator<EmailIsFree, String> {
    private final UserService userService;

    public EmailIsFreeValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !userService.emailExists(value);
    }
}
