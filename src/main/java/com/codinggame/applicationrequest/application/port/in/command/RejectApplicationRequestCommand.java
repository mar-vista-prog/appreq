package com.codinggame.applicationrequest.application.port.in.command;

import jakarta.validation.constraints.NotBlank;

public record RejectApplicationRequestCommand(
    @NotBlank(message = "Rejection reason is required")
    String reason
) {
    public String getReason() {
        return reason;
    }
}
