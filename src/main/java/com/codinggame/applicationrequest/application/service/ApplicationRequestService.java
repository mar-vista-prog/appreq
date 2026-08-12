package com.codinggame.applicationrequest.application.service;

import com.codinggame.applicationrequest.application.dto.ApplicationRequestDto;
import com.codinggame.applicationrequest.application.mapper.ApplicationRequestDtoMapper;
import com.codinggame.applicationrequest.domain.model.ApplicationRequestState;
import com.codinggame.applicationrequest.application.port.in.ApplicationRequestQuery;
import com.codinggame.applicationrequest.application.port.in.ApplicationRequestUseCase;
import com.codinggame.applicationrequest.application.port.in.command.CreateApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.DeleteApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.RejectApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.UpdateApplicationRequestBodyCommand;
import com.codinggame.applicationrequest.application.port.out.ApplicationRequestRepository;
import com.codinggame.applicationrequest.application.port.out.AuditLogRepository;
import com.codinggame.applicationrequest.application.port.out.PublicationIdGenerator;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class ApplicationRequestService implements ApplicationRequestUseCase, ApplicationRequestQuery {

    private final ApplicationRequestCommandService commandService;
    private final ApplicationRequestQueryService queryService;

    public ApplicationRequestService(
            ApplicationRequestRepository requestApplicationRepository,
            AuditLogRepository auditLogRepository,
            PublicationIdGenerator publicationIdGenerator
    ) {
        ApplicationRequestDtoMapper mapper = new ApplicationRequestDtoMapper();
        this.commandService = new ApplicationRequestCommandService(
                requestApplicationRepository,
                auditLogRepository,
                publicationIdGenerator,
                mapper
        );
        this.queryService = new ApplicationRequestQueryService(requestApplicationRepository, mapper);
    }

    public ApplicationRequestDto createRequest(CreateApplicationRequestCommand request) {
        return commandService.createRequest(request);
    }

    public ApplicationRequestDto updateRequestBody(String id, UpdateApplicationRequestBodyCommand request) {
        return commandService.updateRequestBody(id, request);
    }

    public ApplicationRequestDto getRequest(String id) {
        return queryService.getRequest(id);
    }

    public Page<ApplicationRequestDto> listRequests(int page, int size, String name, ApplicationRequestState state) {
        return queryService.listRequests(page, size, name, state);
    }

    public ApplicationRequestDto verifyRequest(String id) {
        return commandService.verifyRequest(id);
    }

    public ApplicationRequestDto acceptRequest(String id) {
        return commandService.acceptRequest(id);
    }

    public ApplicationRequestDto rejectRequest(String id, RejectApplicationRequestCommand request) {
        return commandService.rejectRequest(id, request);
    }

    public ApplicationRequestDto publishRequest(String id) {
        return commandService.publishRequest(id);
    }

    public ApplicationRequestDto deleteRequest(String id, DeleteApplicationRequestCommand request) {
        return commandService.deleteRequest(id, request);
    }
}
