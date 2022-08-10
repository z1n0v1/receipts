package eu.zinovi.receipts.domain.model.validation;

import eu.zinovi.receipts.service.CategoryService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CategoryExistsValidator implements ConstraintValidator<CategoryExists, String> {

    private final CategoryService categoryService;

    public CategoryExistsValidator(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return categoryService.existsByName(value);
    }
}
