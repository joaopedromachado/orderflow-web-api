package br.com.orderflow.mapper.order;

import br.com.orderflow.api.controller.v1.order.request.OrderItemRequest;
import br.com.orderflow.api.controller.v1.order.response.OrderItemResponse;
import br.com.orderflow.api.controller.v1.order.response.OrderResponse;
import br.com.orderflow.domain.order.Order;
import br.com.orderflow.domain.order.OrderItem;
import br.com.orderflow.domain.product.Product;

public class OrderMapper {

    private OrderMapper() {
    }

    public static Order toOrder() {
        return new Order.Builder().build();
    }

    public static OrderItem toOrderItem(final Order order,
                                        final Product product,
                                        final OrderItemRequest orderItemRequest) {
        return new OrderItem.Builder()
                .order(order)
                .product(product)
                .unitPrice(product.getPrice())
                .quantity(orderItemRequest.quantity())
                .build();
    }

    public static OrderResponse toOrderResponse(final Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getUser().getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getItems().stream().map(OrderMapper::toOrderItemResponse).toList(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getProcessedAt(),
                order.getReceiptUrl()
        );
    }

    private static OrderItemResponse toOrderItemResponse(final OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getOrderItemId(),
                orderItem.getProduct().getProductId(),
                orderItem.getUnitPrice(),
                orderItem.getQuantity()
        );
    }
}
