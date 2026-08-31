package br.com.orderflow.exception;

import jakarta.annotation.Nullable;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(@Nullable String message) {
        super(message);
    }
}
