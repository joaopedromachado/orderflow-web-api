package br.com.orderflow.exception;

import jakarta.annotation.Nullable;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(@Nullable String message) {
        super(message);
    }
}
