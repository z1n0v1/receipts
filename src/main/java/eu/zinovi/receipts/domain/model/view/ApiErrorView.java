package eu.zinovi.receipts.domain.model.view;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Data
public class ApiErrorView {

    private HttpStatus status;
    private String message;
    private List<String> errors;

    public ApiErrorView(HttpStatus status, String message, List<String> errors) {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApiErrorView(HttpStatus status, String message, String error) {
        super();
        this.status = status;
        this.message = message;
        errors = Collections.singletonList(error);
    }
}
