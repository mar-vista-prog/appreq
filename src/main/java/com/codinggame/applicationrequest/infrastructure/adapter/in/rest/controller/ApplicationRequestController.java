package com.codinggame.applicationrequest.infrastructure.adapter.in.rest.controller;

import com.codinggame.applicationrequest.application.dto.ApplicationRequestDto;
import com.codinggame.applicationrequest.application.service.ApplicationRequestService;
import com.codinggame.applicationrequest.domain.model.ApplicationRequestState;
import com.codinggame.applicationrequest.application.port.in.command.CreateApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.DeleteApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.RejectApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.UpdateApplicationRequestBodyCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = {"/api/application-requests"})
@RequiredArgsConstructor
public class ApplicationRequestController {

    private final ApplicationRequestService applicationRequestService;

    @PostMapping
    public ResponseEntity<ApplicationRequestDto> createApplicationRequest(
            @Valid @RequestBody CreateApplicationRequestCommand request) {
        ApplicationRequestDto created = applicationRequestService.createRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationRequestDto> getApplicationRequest(@PathVariable String id) {
        ApplicationRequestDto request = applicationRequestService.getRequest(id);
        return ResponseEntity.ok(request);
    }

    @GetMapping
    public ResponseEntity<Page<ApplicationRequestDto>> listApplicationRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ApplicationRequestState state) {
        if (page < 1) {
            throw new IllegalArgumentException("Page must be greater than or equal to 1");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Size must be greater than or equal to 1");
        }
        if (size > 100) {
            throw new IllegalArgumentException("Size must be less than or equal to 100");
        }

        Page<ApplicationRequestDto> requests = applicationRequestService.listRequests(page, size, name, state);
        return ResponseEntity.ok(requests);
    }

    @PatchMapping(path = {"/{id}"})
    public ResponseEntity<ApplicationRequestDto> updateApplicationRequestBody(
            @PathVariable String id,
            @Valid @RequestBody UpdateApplicationRequestBodyCommand request) {
        ApplicationRequestDto updated = applicationRequestService.updateRequestBody(id, request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<ApplicationRequestDto> verifyApplicationRequest(@PathVariable String id) {
        ApplicationRequestDto verified = applicationRequestService.verifyRequest(id);
        return ResponseEntity.ok(verified);
    }

    @PostMapping(path = {"/{id}/accept", "/{id}/approve"})
    public ResponseEntity<ApplicationRequestDto> acceptApplicationRequest(@PathVariable String id) {
        ApplicationRequestDto accepted = applicationRequestService.acceptRequest(id);
        return ResponseEntity.ok(accepted);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApplicationRequestDto> rejectApplicationRequest(
            @PathVariable String id,
            @Valid @RequestBody RejectApplicationRequestCommand request) {
        ApplicationRequestDto rejected = applicationRequestService.rejectRequest(id, request);
        return ResponseEntity.ok(rejected);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ApplicationRequestDto> publishApplicationRequest(@PathVariable String id) {
        ApplicationRequestDto published = applicationRequestService.publishRequest(id);
        return ResponseEntity.ok(published);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApplicationRequestDto> deleteApplicationRequest(
            @PathVariable String id,
            @Valid @RequestBody DeleteApplicationRequestCommand request) {
        ApplicationRequestDto deleted = applicationRequestService.deleteRequest(id, request);
        return ResponseEntity.ok(deleted);
    }
}
