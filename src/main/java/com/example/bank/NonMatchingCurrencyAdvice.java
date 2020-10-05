package com.example.bank;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NonMatchingCurrencyAdvice {
    @ResponseBody
    @ExceptionHandler(NonMatchingCurrencyException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    String nonMatchingCurrencyHandler(NonMatchingCurrencyException ex) {
        return ex.getMessage();
    }
}
