package com.booklog.booklogbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class NaverApiException extends RuntimeException {
    public NaverApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public NaverApiException(String message) {
        super(message);
    }
}
