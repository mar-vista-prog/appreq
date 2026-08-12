package com.codinggame.applicationrequest.domain.exception;

public abstract class ApplicationRequestException extends RuntimeException {
    public ApplicationRequestException(String message) {
        super(message);
    }
    public ApplicationRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
