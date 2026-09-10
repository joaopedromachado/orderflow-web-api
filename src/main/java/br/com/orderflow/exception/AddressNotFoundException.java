package br.com.orderflow.exception;

import jakarta.annotation.Nullable;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(@Nullable final String message) {
        super(message);
    }
}
