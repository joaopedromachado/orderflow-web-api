package br.com.orderflow.api.controller.v1.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressRequest(
        @NotBlank
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve conter 8 dígitos")
        String cep,

        @NotBlank
        String number,

        String complement
) {
}
