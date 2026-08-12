package com.codinggame.applicationrequest.application.service;

import com.codinggame.applicationrequest.application.dto.ApplicationRequestDto;
import com.codinggame.applicationrequest.application.mapper.ApplicationRequestDtoMapper;
import com.codinggame.applicationrequest.domain.exception.RequestNotFoundException;
import com.codinggame.applicationrequest.domain.model.ApplicationRequest;
import com.codinggame.applicationrequest.domain.model.ApplicationRequestState;
import com.codinggame.applicationrequest.application.port.in.ApplicationRequestQuery;
import com.codinggame.applicationrequest.application.port.out.ApplicationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationRequestQueryService implements ApplicationRequestQuery {

    private final ApplicationRequestRepository requestApplicationRepository;
    private final ApplicationRequestDtoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public ApplicationRequestDto getRequest(String id) {
        ApplicationRequest request = requestApplicationRepository.findById(id)
                .orElseThrow(() -> new RequestNotFoundException(id));
        return mapper.toDTO(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationRequestDto> listRequests(int page, int size, String name, ApplicationRequestState state) {
        Page<ApplicationRequest> result;
        if (name != null && state != null) {
            result = requestApplicationRepository.findByNameAndState(name, state, page, size);
        } else if (name != null) {
            result = requestApplicationRepository.findByName(name, page, size);
        } else if (state != null) {
            result = requestApplicationRepository.findByState(state, page, size);
        } else {
            result = requestApplicationRepository.findAll(page, size);
        }
        return mapper.toDTOPage(result);
    }
}
