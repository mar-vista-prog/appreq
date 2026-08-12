package com.codinggame.applicationrequest.application.mapper;

import com.codinggame.applicationrequest.application.dto.ApplicationRequestDto;
import com.codinggame.applicationrequest.domain.model.ApplicationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationRequestDtoMapperTest {

    private final ApplicationRequestDtoMapper mapper = new ApplicationRequestDtoMapper();

    @Test
    void shouldMapDomainRequestToDto() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");
        request.verify();
        request.accept();
        request.publish(42L);
        request.setDeletionReason("unused");
        request.setUpdatedAt(LocalDateTime.of(2025, 1, 2, 3, 4, 5));

        ApplicationRequestDto dto = mapper.toDTO(request);

        assertNotNull(dto);
        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getName(), dto.getName());
        assertEquals(request.getBody(), dto.getBody());
        assertEquals(request.getState(), dto.getState());
        assertEquals(request.getPublicationId(), dto.getPublicationId());
        assertEquals(request.getDeletionReason(), dto.getDeletionReason());
        assertEquals(request.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    void shouldMapPageOfDomainRequestsToDtoPage() {
        ApplicationRequest request = ApplicationRequest.create("Test Request", "Test body");
        Page<ApplicationRequest> page = new PageImpl<>(List.of(request));

        Page<ApplicationRequestDto> dtoPage = mapper.toDTOPage(page);

        assertEquals(1, dtoPage.getTotalElements());
        assertEquals("Test Request", dtoPage.getContent().get(0).getName());
    }
}
