package com.codinggame.applicationrequest.domain.model;

public enum ApplicationRequestState {
    CREATED,
    VERIFIED,
    REJECTED,
    ACCEPTED,
    PUBLISHED,
    DELETED;

    public boolean canTransitionTo(ApplicationRequestState nextState) {
        return switch (this) {
            case CREATED -> nextState == VERIFIED || nextState == DELETED;
            case VERIFIED -> nextState == REJECTED || nextState == ACCEPTED;
            case ACCEPTED -> nextState == REJECTED || nextState == PUBLISHED;
            case REJECTED, PUBLISHED, DELETED -> false;
        };
    }
}
