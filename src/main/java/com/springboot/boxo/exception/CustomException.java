package com.springboot.boxo.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    public CustomException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public CustomException(String message, HttpStatus status, String message1) {
        super(message);
        this.status = status;
        this.message = message1;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getStatusCode() {
        return status;
    }
}
