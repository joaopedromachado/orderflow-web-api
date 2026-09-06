package br.com.orderflow.api.controller.v1.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginUserResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("expire_in")
        Long expireIn
) {}
