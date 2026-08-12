package com.codinggame.applicationrequest.infrastructure.adapter.in.rest.handler;

import com.codinggame.applicationrequest.domain.exception.*;
import com.codinggame.applicationrequest.infrastructure.adapter.in.rest.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle request not found exceptions
     * Returns 404 Not Found
     */
    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRequestNotFound(
            RequestNotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "Not Found",
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handle domain validation exceptions
     * Returns 400 Bad Request
     */
    @ExceptionHandler({
            InvalidRequestNameException.class,
            InvalidRequestBodyException.class,
            InvalidRejectionReasonException.class,
            InvalidDeletionReasonException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            ApplicationRequestException e) {
        log.warn("Validation error: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "Bad Request",
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle invalid state transition exceptions
     * Returns 409 Conflict
     */
    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<ErrorResponse> handleStateTransitionException(
            InvalidStateTransitionException e) {
        log.warn("Invalid state transition: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "Conflict",
                HttpStatus.CONFLICT.value(),
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handle concurrent modification exceptions
     * Returns 409 Conflict
     */
    @ExceptionHandler(ConcurrentModificationException.class)
    public ResponseEntity<ErrorResponse> handleConcurrentModification(
            ConcurrentModificationException e) {
        log.warn("Concurrent modification: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "Conflict",
                HttpStatus.CONFLICT.value(),
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handle validation errors from @Valid annotations
     * Returns 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e) {
        log.warn("Validation error: {}", e.getMessage());

        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((e1, e2) -> e1 + "; " + e2)
                .orElse("Validation failed");

        ErrorResponse errorResponse = new ErrorResponse(
                "Validation Failed",
                HttpStatus.BAD_REQUEST.value(),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle malformed JSON and invalid request body payloads
     * Returns 400 Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException e) {
        log.warn("Malformed request body: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "Bad Request",
                HttpStatus.BAD_REQUEST.value(),
                "Malformed JSON request body or invalid field value.",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle invalid request parameter values (e.g. wrong enum)
     * Returns 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException e) {
        log.warn("Invalid request parameter: {}", e.getMessage());

        String message = String.format(
                "Invalid value '%s' for parameter '%s'.",
                e.getValue(),
                e.getName()
        );

        ErrorResponse errorResponse = new ErrorResponse(
                "Bad Request",
                HttpStatus.BAD_REQUEST.value(),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle illegal argument exceptions
     * Returns 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "Bad Request",
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle framework exceptions with explicit HTTP status codes
     */
    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ErrorResponse> handleErrorResponseException(
            ErrorResponseException e) {
        HttpStatusCode statusCode = e.getStatusCode();
        HttpStatus httpStatus = HttpStatus.resolve(statusCode.value());
        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String message = e.getBody().getDetail() != null
                ? e.getBody().getDetail()
                : httpStatus.getReasonPhrase();

        log.warn("Request failed with status {}: {}", statusCode.value(), message);

        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus.getReasonPhrase(),
                statusCode.value(),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.status(statusCode).body(errorResponse);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Throwable e) {
        Throwable rootCause = getRootCause(e);
        HttpStatus status = resolveStatus(rootCause);
        String message = resolveMessage(e, rootCause, status);

        if (status.is5xxServerError()) {
            log.error("Unexpected exception occurred", e);
        } else {
            log.warn("Handled exception with status {}: {}", status.value(), message);
        }

        ErrorResponse errorResponse = new ErrorResponse(
                status.getReasonPhrase(),
                status.value(),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    private HttpStatus resolveStatus(Throwable rootCause) {
        if (rootCause instanceof RequestNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        if (rootCause instanceof InvalidStateTransitionException
                || rootCause instanceof ConcurrentModificationException) {
            return HttpStatus.CONFLICT;
        }
        if (rootCause instanceof ApplicationRequestException
                || rootCause instanceof IllegalArgumentException
                || rootCause instanceof MethodArgumentNotValidException
                || rootCause instanceof MethodArgumentTypeMismatchException
                || rootCause instanceof HttpMessageNotReadableException) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String resolveMessage(Throwable exception, Throwable rootCause, HttpStatus status) {
        if (rootCause != null && rootCause.getMessage() != null && !rootCause.getMessage().isBlank()) {
            return rootCause.getMessage();
        }
        if (exception.getMessage() != null && !exception.getMessage().isBlank()) {
            return exception.getMessage();
        }
        return status.is5xxServerError() ? "An unexpected error occurred." : status.getReasonPhrase();
    }

    private Throwable getRootCause(Throwable exception) {
        Throwable current = exception;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }
}
