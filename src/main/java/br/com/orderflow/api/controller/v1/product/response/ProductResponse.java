package br.com.orderflow.api.controller.v1.product.response;

import br.com.orderflow.domain.order.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
    UUID productId,
    List<OrderItem> items,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    boolean active
) {}
