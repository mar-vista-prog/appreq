package com.codinggame.applicationrequest.application.port.out;

import com.codinggame.applicationrequest.domain.model.ApplicationRequest;
import com.codinggame.applicationrequest.domain.model.ApplicationRequestState;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ApplicationRequestRepository {
    ApplicationRequest save(ApplicationRequest request);
    Optional<ApplicationRequest> findById(String id);
    Page<ApplicationRequest> findAll(int page, int size);
    Page<ApplicationRequest> findByName(String name, int page, int size);
    Page<ApplicationRequest> findByState(ApplicationRequestState state, int page, int size);
    Page<ApplicationRequest> findByNameAndState(String name, ApplicationRequestState state, int page, int size);
}
