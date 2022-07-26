package eu.zinovi.receipts.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class RequiredParamsNotFound extends RuntimeException {
    public RequiredParamsNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public RequiredParamsNotFound(String message) {
        super(message);
    }

    public RequiredParamsNotFound() {
        super("Не са подадени необходимите параметри");
    }
}
