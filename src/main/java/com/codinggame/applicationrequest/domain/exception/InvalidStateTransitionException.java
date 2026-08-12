package com.codinggame.applicationrequest.domain.exception;

/**
 * Thrown when an invalid state transition is attempted.
 * For example, trying to publish a request in REJECTED state.
 */
public class InvalidStateTransitionException extends ApplicationRequestException {
    public InvalidStateTransitionException(String message) {
        super(message);
    }
}
