package com.codinggame.applicationrequest.domain.model;

import com.codinggame.applicationrequest.domain.exception.InvalidDeletionReasonException;
import com.codinggame.applicationrequest.domain.exception.InvalidStateTransitionException;
import com.codinggame.applicationrequest.domain.exception.InvalidRequestBodyException;
import com.codinggame.applicationrequest.domain.exception.InvalidRequestNameException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationRequestTest {

    @Test
    void createInitializesCreatedRequest() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");

        assertEquals(ApplicationRequestState.CREATED, request.getState());
        assertEquals("Test Request", request.getName());
        assertEquals("Test body", request.getBody());
        assertNotNull(request.getCreatedAt());
        assertNotNull(request.getUpdatedAt());
    }

    @Test
    void updateBodyAllowedInCreatedState() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");

        request.updateBody("Updated body");

        assertEquals("Updated body", request.getBody());
    }

    @Test
    void updateBodyAllowedInVerifiedState() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");
        request.verify();

        request.updateBody("Verified update");

        assertEquals("Verified update", request.getBody());
    }

    @Test
    void updateBodyRejectedInRejectedState() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");
        request.verify();
        request.reject("Not valid");

        assertThrows(InvalidStateTransitionException.class, () -> request.updateBody("Another body"));
    }

    @Test
    void rejectTransitionsToRejectedState() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");
        request.verify();

        request.reject("Not valid");

        assertEquals(ApplicationRequestState.REJECTED, request.getState());
        assertEquals("Not valid", request.getRejectionReason());
    }

    @Test
    void acceptTransitionsToAcceptedState() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");
        request.verify();

        request.accept();

        assertEquals(ApplicationRequestState.ACCEPTED, request.getState());
    }

    @Test
    void publishRequiresAcceptedState() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");
        request.verify();

        assertThrows(InvalidStateTransitionException.class, () -> request.publish(1000000001L));
        assertEquals(ApplicationRequestState.VERIFIED, request.getState());
    }

    @Test
    void publishSucceedsFromAcceptedState() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");
        request.verify();
        request.accept();

        request.publish(1000000001L);

        assertEquals(ApplicationRequestState.PUBLISHED, request.getState());
        assertEquals(1000000001L, request.getPublicationId());
    }

    @Test
    void deleteRequiresReason() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");

        assertThrows(InvalidDeletionReasonException.class, () -> request.delete(" "));
        assertEquals(ApplicationRequestState.CREATED, request.getState());
    }

    @Test
    void createRejectsBlankName() {
        assertThrows(InvalidRequestNameException.class, () -> ApplicationRequest.create("   ", "Test body"));
    }

    @Test
    void createRejectsBlankBody() {
        assertThrows(InvalidRequestBodyException.class, () -> ApplicationRequest.create("Test Request", " "));
    }
}
