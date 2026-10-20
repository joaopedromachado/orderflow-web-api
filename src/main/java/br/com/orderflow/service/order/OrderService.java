package br.com.orderflow.service.order;

import br.com.orderflow.api.controller.v1.order.request.OrderItemRequest;
import br.com.orderflow.api.controller.v1.order.request.OrderRequest;
import br.com.orderflow.api.controller.v1.order.response.OrderResponse;
import br.com.orderflow.domain.order.Order;
import br.com.orderflow.domain.order.OrderItem;
import br.com.orderflow.domain.order.OrderStatus;
import br.com.orderflow.domain.product.Product;
import br.com.orderflow.domain.user.User;
import br.com.orderflow.mapper.order.OrderMapper;
import br.com.orderflow.repository.order.OrderRepository;
import br.com.orderflow.service.product.ProductService;
import br.com.orderflow.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ProductService productService;

    public OrderService(final OrderRepository orderRepository, final UserService userService, final ProductService productService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.productService = productService;
    }

    @Transactional
    public OrderResponse createOrder(final OrderRequest orderRequest, final String token) {
        final User user = this.userService.getUserById(token);
        final Order order = OrderMapper.toOrder();
        final List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        order.setUser(user);

        log.info("Iniciando validação de disponibilidade do produto");

        for (final OrderItemRequest item : orderRequest.items()) {
            final Product product = this.productService.getProductById(item.productId());
            final OrderItem orderItem = OrderMapper.toOrderItem(order, product, item);

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.quantity())));
            orderItems.add(orderItem);
            this.productService.reserveStock(product.getProductId(), item.quantity());
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        return OrderMapper.toOrderResponse(this.orderRepository.save(order));
    }

    public List<Order> getAllByOrderStatus(final OrderStatus status) {
        return this.orderRepository.findAllForReportByStatus(status)
                .orElseThrow(RuntimeException::new);
    }
}
