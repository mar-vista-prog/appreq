package com.codinggame.applicationrequest.application.port.in.command;

import jakarta.validation.constraints.NotBlank;

public record CreateApplicationRequestCommand(
    @NotBlank(message = "Name is required")
    String name,
    @NotBlank(message = "Body is required")
    String body
) {
    public String getName() {
        return name;
    }
    public String getBody() {
        return body;
    }
}
