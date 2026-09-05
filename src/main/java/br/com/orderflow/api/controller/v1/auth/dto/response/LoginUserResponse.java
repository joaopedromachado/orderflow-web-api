package br.com.orderflow.api.controller.v1.auth.dto.response;

public record LoginUserResponse(
        String accessToken,
        Long expireIn
) {}
