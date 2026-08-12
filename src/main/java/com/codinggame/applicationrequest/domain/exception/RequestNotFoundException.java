package com.codinggame.applicationrequest.domain.exception;

public class RequestNotFoundException extends ApplicationRequestException {
    public RequestNotFoundException(String id) {
        super("Request with id '" + id + "' not found");
    }
}
