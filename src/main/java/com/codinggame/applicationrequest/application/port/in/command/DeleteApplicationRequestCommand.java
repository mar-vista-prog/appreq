package com.codinggame.applicationrequest.application.port.in.command;

import jakarta.validation.constraints.NotBlank;

public record DeleteApplicationRequestCommand(
    @NotBlank(message = "Deletion reason is required")
    String reason
) {
    public String getReason() {
        return reason;
    }
}
