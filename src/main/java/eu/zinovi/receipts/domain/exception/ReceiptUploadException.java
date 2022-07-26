package eu.zinovi.receipts.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ReceiptUploadException extends RuntimeException{
    public ReceiptUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReceiptUploadException(String message) {
        super(message);
    }

    public ReceiptUploadException() {
        super("Неуспешно качена бележка");
    }
}
