package com.codinggame.applicationrequest.application;

import com.codinggame.applicationrequest.application.dto.ApplicationRequestDto;
import com.codinggame.applicationrequest.application.port.in.command.CreateApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.DeleteApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.RejectApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.UpdateApplicationRequestBodyCommand;
import com.codinggame.applicationrequest.application.port.out.ApplicationRequestRepository;
import com.codinggame.applicationrequest.application.port.out.AuditLogRepository;
import com.codinggame.applicationrequest.application.port.out.PublicationIdGenerator;
import com.codinggame.applicationrequest.application.service.ApplicationRequestService;
import com.codinggame.applicationrequest.domain.exception.InvalidStateTransitionException;
import com.codinggame.applicationrequest.domain.exception.RequestNotFoundException;
import com.codinggame.applicationrequest.domain.model.ApplicationRequest;
import com.codinggame.applicationrequest.domain.model.ApplicationRequestState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApplicationReqestServiceTest {

    private ApplicationRequestRepository requestRepository;
    private AuditLogRepository auditLogRepository;
    private PublicationIdGenerator publicationIdGenerator;
    private ApplicationRequestService service;

    @BeforeEach
    void setUp() {
        requestRepository = Mockito.mock(ApplicationRequestRepository.class);
        auditLogRepository = Mockito.mock(AuditLogRepository.class);
        publicationIdGenerator = Mockito.mock(PublicationIdGenerator.class);
        service = new ApplicationRequestService(requestRepository, auditLogRepository, publicationIdGenerator);
    }

    @Test
    void shouldCreateRequestSuccessfully() {
        when(requestRepository.save(any(ApplicationRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(auditLogRepository.save(any())).thenReturn(null);

        ApplicationRequestDto result = service.createRequest(new CreateApplicationRequestCommand("Test Request", "Test body"));

        assertNotNull(result);
        assertEquals("Test Request", result.getName());
        assertEquals(ApplicationRequestState.CREATED, result.getState());
        verify(requestRepository).save(any(ApplicationRequest.class));
        verify(auditLogRepository).save(any());
    }

    @Test
    void shouldUpdateRequestBody() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Old body");
        when(requestRepository.findById("123")).thenReturn(Optional.of(request));
        when(requestRepository.save(any(ApplicationRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApplicationRequestDto result = service.updateRequestBody("123", new UpdateApplicationRequestBodyCommand("Updated body"));

        assertEquals("Updated body", result.getBody());
        verify(requestRepository).save(any(ApplicationRequest.class));
    }

    @Test
    void shouldListRequestsWithPagination() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");
        when(requestRepository.findAll(1, 10)).thenReturn(new PageImpl<>(List.of(request), PageRequest.of(0, 10), 1));

        Page<ApplicationRequestDto> result = service.listRequests(1, 10, null, null);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(requestRepository).findAll(1, 10);
    }

    @Test
    void shouldVerifyAndAcceptRequest() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");
        request.verify();
        when(requestRepository.findById("123")).thenReturn(Optional.of(request));
        when(requestRepository.save(any(ApplicationRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(auditLogRepository.save(any())).thenReturn(null);

        ApplicationRequestDto accepted = service.acceptRequest("123");

        assertEquals(ApplicationRequestState.ACCEPTED, accepted.getState());
        verify(auditLogRepository).save(any());
    }

    @Test
    void shouldRejectRequestAndRecordAuditLog() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");
        request.verify();
        when(requestRepository.findById("123")).thenReturn(Optional.of(request));
        when(requestRepository.save(any(ApplicationRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(auditLogRepository.save(any())).thenReturn(null);

        ApplicationRequestDto result = service.rejectRequest("123", new RejectApplicationRequestCommand("Not valid"));

        assertNotNull(result);
        assertEquals(ApplicationRequestState.REJECTED, result.getState());
        assertEquals("Not valid", result.getRejectionReason());
        verify(auditLogRepository).save(any());
    }

    @Test
    void shouldPublishRequestAndGeneratePublicationId() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");
        request.verify();
        request.accept();
        when(requestRepository.findById("123")).thenReturn(Optional.of(request));
        when(publicationIdGenerator.generatePublicationId()).thenReturn(42L);
        when(requestRepository.save(any(ApplicationRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(auditLogRepository.save(any())).thenReturn(null);

        ApplicationRequestDto result = service.publishRequest("123");

        assertEquals(ApplicationRequestState.PUBLISHED, result.getState());
        assertEquals(42L, result.getPublicationId());
        verify(publicationIdGenerator).generatePublicationId();
    }

    @Test
    void shouldDeleteRequestAndRecordReason() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");
        when(requestRepository.findById("123")).thenReturn(Optional.of(request));
        when(requestRepository.save(any(ApplicationRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(auditLogRepository.save(any())).thenReturn(null);

        ApplicationRequestDto result = service.deleteRequest("123", new DeleteApplicationRequestCommand("No longer needed"));

        assertEquals(ApplicationRequestState.DELETED, result.getState());
        assertEquals("No longer needed", result.getDeletionReason());
        verify(auditLogRepository).save(any());
    }

    @Test
    void shouldThrowWhenRequestNotFound() {
        when(requestRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class, () -> service.getRequest("999"));
    }

    @Test
    void shouldRejectInvalidPublishTransition() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");
        when(requestRepository.findById("123")).thenReturn(Optional.of(request));

        assertThrows(InvalidStateTransitionException.class, () -> service.publishRequest("123"));
    }
}
