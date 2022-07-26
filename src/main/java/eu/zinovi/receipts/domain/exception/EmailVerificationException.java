package eu.zinovi.receipts.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class EmailVerificationException extends RuntimeException {
    public EmailVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailVerificationException(String message) {
        super(message);
    }

    public EmailVerificationException() {
        super("Възникна грешка при проверката на електронната поща");
    }
}
