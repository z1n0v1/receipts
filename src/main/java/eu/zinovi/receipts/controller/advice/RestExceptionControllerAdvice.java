package eu.zinovi.receipts.controller.advice;

import eu.zinovi.receipts.domain.exception.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
//@ControllerAdvice
public class RestExceptionControllerAdvice extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,

                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            EntityNotFoundException ex) {
        ApiError apiError = new ApiError(NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(RequiredParamsNotFound.class)
    protected ResponseEntity<Object> handleRequiredParamsNotFound(
            RequiredParamsNotFound ex) {
        ApiError apiError = new ApiError(HttpStatus.EXPECTATION_FAILED);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ReceiptUploadException.class)
    protected ResponseEntity<Object> handleReceiptUploadException(
            ReceiptUploadException ex) {
        ApiError apiError = new ApiError(HttpStatus.SERVICE_UNAVAILABLE);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(FieldViolationException.class)
    protected ResponseEntity<Object> handleFieldViolationException(
            FieldViolationException ex) {
        ApiError apiError = new ApiError(HttpStatus.EXPECTATION_FAILED);
        StringBuilder sb = new StringBuilder();
        for (ObjectError error : ex.getErrors()) {
            sb.append(error.getDefaultMessage()).append("\n");
        }
        apiError.setMessage(sb.toString());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleNotLoggedIn(AccessDeniedException ex) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
