package eu.zinovi.receipts.domain.model.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static eu.zinovi.receipts.util.constants.MessageConstants.INVALID_CATEGORY;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = CategoryExistsValidator.class)
public @interface CategoryExists {
    String message() default INVALID_CATEGORY;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
