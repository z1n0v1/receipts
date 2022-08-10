package eu.zinovi.receipts.domain.model.validation;

import eu.zinovi.receipts.service.RoleService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RoleExistsValidator implements ConstraintValidator<RoleExists, String> {

    private final RoleService roleService;

    public RoleExistsValidator(RoleService rolService) {
        this.roleService = rolService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return roleService.existsByName(value);
    }
}
