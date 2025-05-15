package com.booklog.booklogbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class EmailVerificationException extends RuntimeException {

    public EmailVerificationException(String message) {
        super(message);
    }

    public EmailVerificationException(String message, Throwable cause) {}
}
