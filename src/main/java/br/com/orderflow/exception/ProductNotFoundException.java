package br.com.orderflow.exception;

import jakarta.annotation.Nullable;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(@Nullable final String message) {
        super(message);
    }
}
