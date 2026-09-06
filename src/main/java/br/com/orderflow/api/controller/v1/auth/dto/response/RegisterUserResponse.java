package br.com.orderflow.api.controller.v1.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record RegisterUserResponse(
        @JsonProperty("user_id")
        UUID userId
) {
}
