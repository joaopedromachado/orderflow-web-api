package br.com.orderflow.service.product;

import br.com.orderflow.api.controller.v1.product.response.ProductResponse;
import br.com.orderflow.domain.product.Product;
import br.com.orderflow.exception.InsufficientStockException;
import br.com.orderflow.exception.ProductNotFoundException;
import br.com.orderflow.mapper.product.ProductMapper;
import br.com.orderflow.repository.product.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> getProducts(final int page,
                                             final int size) {
        log.info("Iniciando busca de produtos");
        Page<Product> products = this.productRepository.findAll(PageRequest.of(page, size));

        return products.stream()
                .map(ProductMapper::toProductResponse)
                .toList();
    }

    public Product getProductById(final String productId) {
        log.info("Iniciando busca de produtos por id={}", productId);

        return this.productRepository.findById(UUID.fromString(productId))
                .orElseThrow(() -> new ProductNotFoundException("Produto nao encontrado"));
    }

    public ProductResponse saveProduct(final Product product) {
        return ProductMapper.toProductResponse(this.productRepository.save(product));
    }

    public void reserveStock(final UUID productId, final Integer quantity) {
        if (Objects.isNull(quantity) || quantity <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser diferente de null e maior que zero");
        }

        final int updatedProducts = this.productRepository.decreaseStockIfAvailable(productId, quantity);

        if (updatedProducts == 0) {
            throw new InsufficientStockException("Produto indisponível ou estoque insuficiente");
        }
    }

}
