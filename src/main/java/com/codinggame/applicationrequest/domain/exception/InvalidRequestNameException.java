package com.codinggame.applicationrequest.domain.exception;

/**
 * Thrown when request name is invalid (null, blank, etc.)
 */
public class InvalidRequestNameException extends ApplicationRequestException {
    public InvalidRequestNameException(String message) {
        super(message);
    }
    public InvalidRequestNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
