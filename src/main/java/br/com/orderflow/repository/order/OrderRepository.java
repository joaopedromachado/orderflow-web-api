package br.com.orderflow.repository.order;

import br.com.orderflow.domain.order.Order;
import br.com.orderflow.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<List<Order>> findAllByOrderStatus(final OrderStatus status);
}
