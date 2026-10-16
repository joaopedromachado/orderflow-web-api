package br.com.orderflow.api.controller.v1.product;

import br.com.orderflow.api.controller.v1.product.request.ProductRequest;
import br.com.orderflow.api.controller.v1.product.response.ProductResponse;
import br.com.orderflow.mapper.product.ProductMapper;
import br.com.orderflow.service.product.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody final ProductRequest product
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.productService.saveProduct(ProductMapper.toProduct(product)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(defaultValue = "0") final int page
    ) {
        return ResponseEntity.ok(this.productService.getProducts(page, size));
    }
}
