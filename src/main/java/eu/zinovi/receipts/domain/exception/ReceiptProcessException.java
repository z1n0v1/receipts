package eu.zinovi.receipts.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ReceiptProcessException extends RuntimeException{
    public ReceiptProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReceiptProcessException(String message) {
        super(message);
    }

    public ReceiptProcessException() {
        super("Неуспешно качена бележка");
    }
}
