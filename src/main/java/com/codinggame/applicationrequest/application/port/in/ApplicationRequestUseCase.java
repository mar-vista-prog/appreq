package com.codinggame.applicationrequest.application.port.in;

import com.codinggame.applicationrequest.application.dto.ApplicationRequestDto;
import com.codinggame.applicationrequest.application.port.in.command.CreateApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.DeleteApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.RejectApplicationRequestCommand;
import com.codinggame.applicationrequest.application.port.in.command.UpdateApplicationRequestBodyCommand;

public interface ApplicationRequestUseCase {
    ApplicationRequestDto createRequest(CreateApplicationRequestCommand request);
    ApplicationRequestDto updateRequestBody(String id, UpdateApplicationRequestBodyCommand request);
    ApplicationRequestDto verifyRequest(String id);
    ApplicationRequestDto acceptRequest(String id);
    ApplicationRequestDto rejectRequest(String id, RejectApplicationRequestCommand request);
    ApplicationRequestDto publishRequest(String id);
    ApplicationRequestDto deleteRequest(String id, DeleteApplicationRequestCommand request);
}

