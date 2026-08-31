package br.com.orderflow.api.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateUserRequest(
        @NotBlank(message = "Nome do usuário não pode estar em branco")
        @Pattern(
                regexp = "^[a-z][a-z0-9_]{4,13}$",
                message = "Username deve começar com letra minúscula e conter apenas letras minúsculas, números ou _. "
        )
        String username,

        @Email
        String email,

        @NotBlank(message = "Senha do usuário não pode estar em branco")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "Senha precisa ter pelo menos 8 caracteres com letras e números.")
        String password
) {}
