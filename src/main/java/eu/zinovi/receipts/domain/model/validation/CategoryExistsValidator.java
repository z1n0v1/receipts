package eu.zinovi.receipts.domain.model.validation;

import eu.zinovi.receipts.service.impl.CategoryServiceImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CategoryExistsValidator implements ConstraintValidator<CategoryExists, String> {

    private final CategoryServiceImpl categoryServiceImpl;

    public CategoryExistsValidator(CategoryServiceImpl categoryServiceImpl) {
        this.categoryServiceImpl = categoryServiceImpl;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return categoryServiceImpl.existsByName(value);
    }
}
