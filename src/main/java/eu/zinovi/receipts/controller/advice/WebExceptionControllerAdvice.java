package eu.zinovi.receipts.controller.advice;

import eu.zinovi.receipts.domain.exception.EmailVerificationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class WebExceptionControllerAdvice {

    @ExceptionHandler(EmailVerificationException.class)
    protected ModelAndView handleEmailVerificationException(
            EmailVerificationException ex) {

        ModelAndView modelAndView = new ModelAndView("errors/email-verification");
        modelAndView.addObject("error", ex.getMessage());
        return modelAndView;
    }

}
