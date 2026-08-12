package com.codinggame.applicationrequest.infrastructure.adapter.in.rest.handler;

import com.codinggame.applicationrequest.domain.exception.ConcurrentModificationException;
import com.codinggame.applicationrequest.domain.exception.InvalidRequestBodyException;
import com.codinggame.applicationrequest.domain.exception.InvalidStateTransitionException;
import com.codinggame.applicationrequest.domain.exception.RequestNotFoundException;
import com.codinggame.applicationrequest.infrastructure.adapter.in.rest.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldMapRequestNotFoundTo404() {
        ResponseEntity<ErrorResponse> response = handler.handleRequestNotFound(new RequestNotFoundException("req-1"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not Found", response.getBody().getError());
    }

    @Test
    void shouldMapValidationExceptionTo400() {
        ResponseEntity<ErrorResponse> response = handler.handleValidationExceptions(new InvalidRequestBodyException("Body is required"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad Request", response.getBody().getError());
    }

    @Test
    void shouldMapStateTransitionExceptionTo409() {
        ResponseEntity<ErrorResponse> response = handler.handleStateTransitionException(new InvalidStateTransitionException("Bad transition"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflict", response.getBody().getError());
    }

    @Test
    void shouldMapConcurrentModificationTo409() {
        ResponseEntity<ErrorResponse> response = handler.handleConcurrentModification(new ConcurrentModificationException("Concurrent update", new RuntimeException("root")));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflict", response.getBody().getError());
    }

    @Test
    void shouldMapIllegalArgumentTo400() {
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(new IllegalArgumentException("Invalid page"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid page", response.getBody().getMessage());
    }
}
