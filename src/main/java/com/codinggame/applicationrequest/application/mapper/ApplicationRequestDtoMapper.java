package com.codinggame.applicationrequest.application.mapper;

import com.codinggame.applicationrequest.application.dto.ApplicationRequestDto;
import com.codinggame.applicationrequest.domain.model.ApplicationRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRequestDtoMapper {

    public ApplicationRequestDto toDTO(ApplicationRequest request) {
        return ApplicationRequestDto.builder()
                .id(request.getId())
                .name(request.getName())
                .body(request.getBody())
                .state(request.getState())
                .publicationId(request.getPublicationId())
                .rejectionReason(request.getRejectionReason())
                .deletionReason(request.getDeletionReason())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }

    public Page<ApplicationRequestDto> toDTOPage(Page<ApplicationRequest> result) {
        return result.map(this::toDTO);
    }
}
