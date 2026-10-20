package br.com.orderflow.api.controller.v1.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Nome do produto não pode estar em branco.")
        String name,
        String description,
        @NotNull
        BigDecimal price,
        @NotNull
        Integer stock,
        boolean active

) {
}
