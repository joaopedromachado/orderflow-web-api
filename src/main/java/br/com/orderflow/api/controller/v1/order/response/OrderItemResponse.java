package br.com.orderflow.api.controller.v1.order.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID orderItemId,
        UUID productId,
        BigDecimal unitPrice,
        Integer quantity
) {
}
