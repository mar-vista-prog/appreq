package com.codinggame.applicationrequest.application.service;

import com.codinggame.applicationrequest.application.dto.ApplicationRequestDto;
import com.codinggame.applicationrequest.application.mapper.ApplicationRequestDtoMapper;
import com.codinggame.applicationrequest.domain.exception.ConcurrentModificationException;
import com.codinggame.applicationrequest.domain.exception.InvalidStateTransitionException;
import com.codinggame.applicationrequest.domain.exception.RequestNotFoundException;
import com.codinggame.applicationrequest.domain.model.ApplicationRequest;
import com.codinggame.applicationrequest.domain.model.ApplicationRequestState;
import com.codinggame.applicationrequest.domain.model.AuditLogEntry;
import com.codinggame.applicationrequest.application.port.in.ApplicationRequestUseCase;
import com.codinggame.applicationrequest.application.port.in.command.CreateApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.DeleteApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.RejectApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.UpdateApplicationRequestBodyCommand;
import com.codinggame.applicationrequest.application.port.out.ApplicationRequestRepository;
import com.codinggame.applicationrequest.application.port.out.AuditLogRepository;
import com.codinggame.applicationrequest.application.port.out.PublicationIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ApplicationRequestCommandService implements ApplicationRequestUseCase {

    private final ApplicationRequestRepository requestApplicationRepository;
    private final AuditLogRepository auditLogRepository;
    private final PublicationIdGenerator publicationIdGenerator;
    private final ApplicationRequestDtoMapper mapper;

    @Override
    @Transactional
    public ApplicationRequestDto createRequest(CreateApplicationRequestCommand request) {
        ApplicationRequest domainRequest = ApplicationRequest.create(request.getName(), request.getBody());
        ApplicationRequest savedRequest = requestApplicationRepository.save(domainRequest);
        recordAuditLog(savedRequest, null, ApplicationRequestState.CREATED, null);
        return mapper.toDTO(savedRequest);
    }

    @Override
    @Transactional
    public ApplicationRequestDto updateRequestBody(String id, UpdateApplicationRequestBodyCommand request) {
        return executeWithConcurrencyHandling(() -> {
            ApplicationRequest domainRequest = findRequestOrThrow(id);
            domainRequest.updateBody(request.getBody());
            ApplicationRequest updatedRequest = requestApplicationRepository.save(domainRequest);
            return mapper.toDTO(updatedRequest);
        });
    }

    @Override
    @Transactional
    public ApplicationRequestDto verifyRequest(String id) {
        return executeWithConcurrencyHandling(() -> {
            ApplicationRequest domainRequest = findRequestOrThrow(id);
            ApplicationRequestState previousState = domainRequest.getState();
            domainRequest.verify();
            ApplicationRequest updatedRequest = requestApplicationRepository.save(domainRequest);
            recordAuditLog(updatedRequest, previousState, ApplicationRequestState.VERIFIED, null);
            return mapper.toDTO(updatedRequest);
        });
    }

    @Override
    @Transactional
    public ApplicationRequestDto acceptRequest(String id) {
        return executeWithConcurrencyHandling(() -> {
            ApplicationRequest domainRequest = findRequestOrThrow(id);
            ApplicationRequestState previousState = domainRequest.getState();
            domainRequest.accept();
            ApplicationRequest updatedRequest = requestApplicationRepository.save(domainRequest);
            recordAuditLog(updatedRequest, previousState, ApplicationRequestState.ACCEPTED, null);
            return mapper.toDTO(updatedRequest);
        });
    }

    @Override
    @Transactional
    public ApplicationRequestDto rejectRequest(String id, RejectApplicationRequestCommand request) {
        return executeWithConcurrencyHandling(() -> {
            ApplicationRequest domainRequest = findRequestOrThrow(id);
            ApplicationRequestState previousState = domainRequest.getState();
            domainRequest.reject(request.getReason());
            ApplicationRequest updatedRequest = requestApplicationRepository.save(domainRequest);
            recordAuditLog(updatedRequest, previousState, ApplicationRequestState.REJECTED, request.getReason());
            return mapper.toDTO(updatedRequest);
        });
    }

    @Override
    @Transactional
    public ApplicationRequestDto publishRequest(String id) {
        return executeWithConcurrencyHandling(() -> {
            ApplicationRequest domainRequest = findRequestOrThrow(id);

            if (!domainRequest.getState().canTransitionTo(ApplicationRequestState.PUBLISHED)) {
                throw new InvalidStateTransitionException(
                        String.format("Cannot transition from %s to PUBLISHED", domainRequest.getState())
                );
            }

            ApplicationRequestState previousState = domainRequest.getState();
            Long publicationId = publicationIdGenerator.generatePublicationId();
            domainRequest.publish(publicationId);
            ApplicationRequest updatedRequest = requestApplicationRepository.save(domainRequest);
            recordAuditLog(updatedRequest, previousState, ApplicationRequestState.PUBLISHED, null);
            return mapper.toDTO(updatedRequest);
        });
    }

    @Override
    @Transactional
    public ApplicationRequestDto deleteRequest(String id, DeleteApplicationRequestCommand request) {
        return executeWithConcurrencyHandling(() -> {
            ApplicationRequest domainRequest = findRequestOrThrow(id);
            ApplicationRequestState previousState = domainRequest.getState();
            domainRequest.delete(request.getReason());
            ApplicationRequest updatedRequest = requestApplicationRepository.save(domainRequest);
            recordAuditLog(updatedRequest, previousState, ApplicationRequestState.DELETED, request.getReason());
            return mapper.toDTO(updatedRequest);
        });
    }

    private ApplicationRequest findRequestOrThrow(String id) {
        return requestApplicationRepository.findById(id)
                .orElseThrow(() -> new RequestNotFoundException(id));
    }

    private void recordAuditLog(
            ApplicationRequest request,
            ApplicationRequestState previousState,
            ApplicationRequestState newState,
            String reason
    ) {
        AuditLogEntry entry = AuditLogEntry.create(request.getId(), previousState, newState, reason);
        auditLogRepository.save(entry);
    }

    private <T> T executeWithConcurrencyHandling(Supplier<T> operation) {
        try {
            return operation.get();
        } catch (ConcurrentModificationException e) {
            throw e;
        }
    }
}
