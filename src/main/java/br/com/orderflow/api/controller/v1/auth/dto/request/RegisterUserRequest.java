package br.com.orderflow.api.controller.v1.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
        @NotBlank(message = "Nome do usuário não pode estar em branco")
        String username,

        @Email
        String email,

        @NotBlank(message = "Senha do usuário não pode estar em branco")
        String password
) {}
