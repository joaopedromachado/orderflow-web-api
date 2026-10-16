package br.com.orderflow.api.controller.v1.order.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(
        @NotNull
        String productId,

        @NotNull
        @Positive
        Integer quantity
) {
}
