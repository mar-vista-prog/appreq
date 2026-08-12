package com.codinggame.applicationrequest.domain.exception;

/**
 * Thrown when request body is invalid (null, blank, etc.)
 */
public class InvalidRequestBodyException extends ApplicationRequestException {
    public InvalidRequestBodyException(String message) {
        super(message);
    }
}
