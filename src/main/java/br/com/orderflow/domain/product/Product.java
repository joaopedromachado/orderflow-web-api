package br.com.orderflow.domain.product;

import br.com.orderflow.domain.order.OrderItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private UUID productId;

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY
    )
    private List<OrderItem> items;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private boolean active;

    public Product(Builder builder) {
        this.productId = builder.productId;
        this.items = builder.items;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.stock = builder.stock;
        this.active = builder.active;
    }

    public Product() {
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static class Builder {
        private UUID productId;
        private List<OrderItem> items;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stock;
        private boolean active;

        public Builder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public Builder items(List<OrderItem> items) {
            this.items = items;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder stock(Integer stock) {
            this.stock = stock;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}
