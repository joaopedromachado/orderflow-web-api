package br.com.orderflow.mapper.product;

import br.com.orderflow.api.controller.v1.product.request.ProductRequest;
import br.com.orderflow.api.controller.v1.product.response.ProductResponse;
import br.com.orderflow.domain.product.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public static ProductResponse toProductResponse(final Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getItems(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.isActive());
    }

    public static Product toProduct(final ProductRequest request) {
        return new Product.Builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .active(request.active())
                .build();
    }
}
