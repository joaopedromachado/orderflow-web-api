package br.com.orderflow.repository.order;

import br.com.orderflow.domain.order.Order;
import br.com.orderflow.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query(value = """
            select distinct o
            from Order o
            join fetch o.user
            left join fetch o.items item
            left join fetch item.product
            where o.status = :status
            """)
    Optional<List<Order>> findAllForReportByStatus(OrderStatus status);
}
