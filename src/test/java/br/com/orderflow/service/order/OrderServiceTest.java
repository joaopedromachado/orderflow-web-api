package br.com.orderflow.service.order;

import br.com.orderflow.api.controller.v1.order.request.OrderItemRequest;
import br.com.orderflow.api.controller.v1.order.request.OrderRequest;
import br.com.orderflow.api.controller.v1.order.response.OrderResponse;
import br.com.orderflow.domain.order.Order;
import br.com.orderflow.domain.product.Product;
import br.com.orderflow.domain.user.User;
import br.com.orderflow.repository.order.OrderRepository;
import br.com.orderflow.service.product.ProductService;
import br.com.orderflow.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Test
    @DisplayName("Deve criar pedido com total calculado pelo preço e quantidade dos itens.")
    void deveCriarPedido_quandoItensValidos() {
        final UUID userId = UUID.randomUUID();
        final UUID firstProductId = UUID.randomUUID();
        final UUID secondProductId = UUID.randomUUID();
        final User user = new User.Builder().userId(userId).build();
        final Product firstProduct = new Product.Builder()
                .productId(firstProductId)
                .price(new BigDecimal("12.50"))
                .build();
        final Product secondProduct = new Product.Builder()
                .productId(secondProductId)
                .price(new BigDecimal("7.25"))
                .build();
        final OrderRequest request = new OrderRequest(List.of(
                new OrderItemRequest(firstProductId.toString(), 2),
                new OrderItemRequest(secondProductId.toString(), 3)
        ));
        final OrderService orderService = new OrderService(
                this.orderRepository,
                this.userService,
                this.productService
        );

        when(this.userService.getUserById(userId.toString())).thenReturn(user);
        when(this.productService.getProductById(firstProductId.toString())).thenReturn(firstProduct);
        when(this.productService.getProductById(secondProductId.toString())).thenReturn(secondProduct);
        when(this.orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final OrderResponse response = orderService.createOrder(request, userId.toString());
        final ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        verify(this.orderRepository).save(orderCaptor.capture());
        verify(this.productService).reserveStock(firstProductId, 2);
        verify(this.productService).reserveStock(secondProductId, 3);

        final Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getUser()).isSameAs(user);
        assertThat(savedOrder.getItems()).hasSize(2);
        assertThat(savedOrder.getItems())
                .allSatisfy(item -> assertThat(item.getOrder()).isSameAs(savedOrder));
        assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo("46.75");
        assertThat(response.totalAmount()).isEqualByComparingTo("46.75");
        assertThat(response.userId()).isEqualTo(userId);
    }
}
