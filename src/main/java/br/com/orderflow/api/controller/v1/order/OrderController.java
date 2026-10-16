package br.com.orderflow.api.controller.v1.order;

import br.com.orderflow.api.controller.v1.order.request.OrderRequest;
import br.com.orderflow.api.controller.v1.order.response.OrderResponse;
import br.com.orderflow.service.order.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody @Valid final OrderRequest orderRequest,
            final JwtAuthenticationToken token
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.createOrder(orderRequest, token.getName()));
    }
}
