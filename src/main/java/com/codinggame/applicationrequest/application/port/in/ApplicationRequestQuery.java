package com.codinggame.applicationrequest.application.port.in;

import com.codinggame.applicationrequest.application.dto.ApplicationRequestDto;
import com.codinggame.applicationrequest.domain.model.ApplicationRequestState;
import org.springframework.data.domain.Page;

public interface ApplicationRequestQuery {
    ApplicationRequestDto getRequest(String id);
    Page<ApplicationRequestDto> listRequests(int page, int size, String name, ApplicationRequestState state);
}

