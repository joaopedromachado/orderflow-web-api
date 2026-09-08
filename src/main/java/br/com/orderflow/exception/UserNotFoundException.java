package br.com.orderflow.exception;

import jakarta.annotation.Nullable;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(@Nullable final String message) {
        super(message);
    }
}
