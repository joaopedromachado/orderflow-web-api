package br.com.orderflow.exception;

import jakarta.annotation.Nullable;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(@Nullable final String message) {
        super(message);
    }
}
