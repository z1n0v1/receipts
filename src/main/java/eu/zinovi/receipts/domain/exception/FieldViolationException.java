package eu.zinovi.receipts.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class FieldViolationException extends RuntimeException {
    private Collection<ObjectError> errors;
    public FieldViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FieldViolationException(String message) {
        super(message);
    }

    public FieldViolationException() {
        super("Възникна грешка при проверката на полетата");
    }

    public FieldViolationException(Collection<ObjectError> errors) {
        super("Възникна грешка при проверката на полетата");
        this.errors = errors;
    }

    public Collection<ObjectError> getErrors() {
        return errors;
    }
}
