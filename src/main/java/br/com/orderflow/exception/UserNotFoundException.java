package br.com.orderflow.exception;

import jakarta.annotation.Nullable;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(@Nullable String message) {
        super(message);
    }
}
