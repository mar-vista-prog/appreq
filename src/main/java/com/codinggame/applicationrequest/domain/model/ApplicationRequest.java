package com.codinggame.applicationrequest.domain.model;

import com.codinggame.applicationrequest.domain.exception.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequest {
    private String id;
    private String name;
    private String body;
    private ApplicationRequestState state;
    private Long publicationId;
    private String rejectionReason;
    private String deletionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;

    public static ApplicationRequest create(String name, String body) {
        if (name == null || name.isBlank()) {
            throw new InvalidRequestNameException("Request name is required");
        }
        if (body == null || body.isBlank()) {
            throw new InvalidRequestBodyException("Request body is required");
        }

        ApplicationRequest request = new ApplicationRequest();
        request.id = UUID.randomUUID().toString();
        request.name = name;
        request.body = body;
        request.state = ApplicationRequestState.CREATED;
        request.createdAt = LocalDateTime.now();
        request.updatedAt = LocalDateTime.now();
        return request;
    }

    public void updateBody(String newBody) {
        if (state != ApplicationRequestState.CREATED && state != ApplicationRequestState.VERIFIED) {
            throw new InvalidStateTransitionException("Body can only be modified in CREATED or VERIFIED state");
        }
        if (newBody == null || newBody.isBlank()) {
            throw new InvalidRequestBodyException("Body cannot be empty");
        }
        this.body = newBody;
        this.updatedAt = LocalDateTime.now();
    }

    public void verify() {
        transitionTo(ApplicationRequestState.VERIFIED);
    }

    public void accept() {
        transitionTo(ApplicationRequestState.ACCEPTED);
    }

    public void reject(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new InvalidRejectionReasonException("Rejection reason is required");
        }
        this.rejectionReason = reason;
        transitionTo(ApplicationRequestState.REJECTED);
    }

    public void publish(Long publicationId) {
        if (publicationId == null) {
            throw new IllegalArgumentException("Publication ID is required");
        }
        this.publicationId = publicationId;
        transitionTo(ApplicationRequestState.PUBLISHED);
    }

    public void delete(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new InvalidDeletionReasonException("Deletion reason is required");
        }
        this.deletionReason = reason;
        transitionTo(ApplicationRequestState.DELETED);
    }

    private void transitionTo(ApplicationRequestState nextState) {
        if (!state.canTransitionTo(nextState)) {
            throw new InvalidStateTransitionException(
                String.format("Cannot transition from %s to %s", state, nextState)
            );
        }
        this.state = nextState;
        this.updatedAt = LocalDateTime.now();
    }
}
