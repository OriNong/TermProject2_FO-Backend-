package com.booklog.booklogbackend.exception;

public class BadRequestException extends IllegalStateException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
