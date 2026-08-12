package com.codinggame.applicationrequest.application.port.in.command;

import jakarta.validation.constraints.NotBlank;

public record UpdateApplicationRequestBodyCommand(
    @NotBlank(message = "Body is required")
    String body
) {
    public String getBody() {
        return body;
    }
}
