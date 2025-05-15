package com.booklog.booklogbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class UserRegisterException extends RuntimeException {

    private final String email;
    private final String message;

    public UserRegisterException(String email, String message) {
        super(String.format("Failed to register User[%s] : '%s'", email, message));
        this.email = email;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
