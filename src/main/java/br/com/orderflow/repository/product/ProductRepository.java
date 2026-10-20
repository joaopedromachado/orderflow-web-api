package br.com.orderflow.repository.product;

import br.com.orderflow.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Modifying
    @Query(value = """
            UPDATE Product p
            SET p.stock = p.stock - :quantity
            WHERE p.productId = :productId
              AND p.active = true
              AND p.stock >= :quantity
            """)
    int decreaseStockIfAvailable(
            @Param("productId") UUID productId,
            @Param("quantity") Integer quantity
    );
}
