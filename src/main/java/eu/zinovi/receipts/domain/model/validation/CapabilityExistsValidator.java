package eu.zinovi.receipts.domain.model.validation;

import eu.zinovi.receipts.domain.model.enums.CapabilityEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CapabilityExistsValidator implements ConstraintValidator<CapabilityExists, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        for (CapabilityEnum capability : CapabilityEnum.values()) {
            if (capability.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
