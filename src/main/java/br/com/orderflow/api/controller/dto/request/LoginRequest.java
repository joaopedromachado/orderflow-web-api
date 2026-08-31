package br.com.orderflow.api.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Nome do usuário não pode estar em branco")
        String username,
        @NotBlank(message = "Senha do usuário não pode estar em branco")
        String password
) {}
