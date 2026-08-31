package br.com.orderflow.api.controller.dto.response;

public record LoginResponse(String accessToken, Long expireIn) {}
