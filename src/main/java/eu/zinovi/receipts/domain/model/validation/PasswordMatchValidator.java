package eu.zinovi.receipts.domain.model.validation;

import eu.zinovi.receipts.service.impl.UserServiceImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, String> {
private final UserServiceImpl userServiceImpl;

public PasswordMatchValidator(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        }

@Override
public boolean isValid(String value, ConstraintValidatorContext context) {
        return userServiceImpl.checkPassword(value);
        }
}
