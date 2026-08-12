package com.codinggame.applicationrequest.domain.exception;

public class ConcurrentModificationException extends RuntimeException {
    public ConcurrentModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
