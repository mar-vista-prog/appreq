package com.codinggame.applicationrequest.infrastructure.adapter.in.rest.controller;

import com.codinggame.applicationrequest.application.dto.ApplicationRequestDto;
import com.codinggame.applicationrequest.application.port.in.command.CreateApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.DeleteApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.RejectApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.UpdateApplicationRequestBodyCommand;
import com.codinggame.applicationrequest.application.service.ApplicationRequestService;
import com.codinggame.applicationrequest.domain.model.ApplicationRequestState;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationRequestControllerTest {

    private final ApplicationRequestService service = Mockito.mock(ApplicationRequestService.class);
    private final ApplicationRequestController controller = new ApplicationRequestController(service);

    @Test
    void shouldCreateRequestAndReturnCreatedStatus() {
        ApplicationRequestDto dto = new ApplicationRequestDto(
                "id-1",
                "Test Request",
                "Test body",
                ApplicationRequestState.CREATED,
                null,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(service.createRequest(any(CreateApplicationRequestCommand.class))).thenReturn(dto);

        ResponseEntity<ApplicationRequestDto> response = controller.createApplicationRequest(new CreateApplicationRequestCommand("Test Request", "Test body"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void shouldRejectPageBelowOne() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.listApplicationRequests(0, 10, null, null));

        assertEquals("Page must be greater than or equal to 1", ex.getMessage());
    }

    @Test
    void shouldRejectSizeAboveHundred() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.listApplicationRequests(1, 101, null, null));

        assertEquals("Size must be less than or equal to 100", ex.getMessage());
    }

    @Test
    void shouldAcceptDeleteRequest() {
        ApplicationRequestDto dto = new ApplicationRequestDto(
                "id-1",
                "Test Request",
                "Test body",
                ApplicationRequestState.DELETED,
                null,
                null,
                "No longer needed",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(service.deleteRequest(eq("id-1"), any(DeleteApplicationRequestCommand.class))).thenReturn(dto);

        ResponseEntity<ApplicationRequestDto> response = controller.deleteApplicationRequest("id-1", new DeleteApplicationRequestCommand("No longer needed"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ApplicationRequestState.DELETED, response.getBody().getState());
    }

    @Test
    void shouldReturnPageResponseForList() {
        ApplicationRequestDto dto = new ApplicationRequestDto(
                "id-1",
                "Test Request",
                "Test body",
                ApplicationRequestState.CREATED,
                null,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(service.listRequests(1, 10, "Test", ApplicationRequestState.CREATED))
                .thenReturn(new PageImpl<>(List.of(dto), org.springframework.data.domain.PageRequest.of(0, 10), 1));

        ResponseEntity<Page<ApplicationRequestDto>> response = controller.listApplicationRequests(1, 10, "Test", ApplicationRequestState.CREATED);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void shouldRejectInvalidBodyUpdate() {
        when(service.updateRequestBody(eq("id-1"), any(UpdateApplicationRequestBodyCommand.class)))
                .thenThrow(new IllegalArgumentException("Body cannot be empty"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.updateApplicationRequestBody("id-1", new UpdateApplicationRequestBodyCommand("   ")));

        assertEquals("Body cannot be empty", ex.getMessage());
    }

    @Test
    void shouldAcceptRequestWithCanonicalEndpointName() {
        ApplicationRequestDto dto = new ApplicationRequestDto(
                "id-1",
                "Test Request",
                "Test body",
                ApplicationRequestState.ACCEPTED,
                null,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(service.acceptRequest(eq("id-1"))).thenReturn(dto);

        ResponseEntity<ApplicationRequestDto> response = controller.acceptApplicationRequest("id-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ApplicationRequestState.ACCEPTED, response.getBody().getState());
    }

    @Test
    void shouldRejectRequestWithReason() {
        ApplicationRequestDto dto = new ApplicationRequestDto(
                "id-1",
                "Test Request",
                "Test body",
                ApplicationRequestState.REJECTED,
                null,
                "Not valid",
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(service.rejectRequest(eq("id-1"), any(RejectApplicationRequestCommand.class))).thenReturn(dto);

        ResponseEntity<ApplicationRequestDto> response = controller.rejectApplicationRequest("id-1", new RejectApplicationRequestCommand("Not valid"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ApplicationRequestState.REJECTED, response.getBody().getState());
    }
}
