package eu.zinovi.receipts.domain.model.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = EmailExistsValidator.class)
public @interface EmailExists {
    String message() default "Имейл адресът не е наличен в базата на данни.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
