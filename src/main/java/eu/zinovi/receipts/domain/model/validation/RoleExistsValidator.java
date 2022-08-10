package eu.zinovi.receipts.domain.model.validation;

import eu.zinovi.receipts.service.impl.RoleServiceImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RoleExistsValidator implements ConstraintValidator<RoleExists, String> {

    private final RoleServiceImpl roleServiceImpl;

    public RoleExistsValidator(RoleServiceImpl rolService) {
        this.roleServiceImpl = rolService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return roleServiceImpl.existsByName(value);
    }
}
