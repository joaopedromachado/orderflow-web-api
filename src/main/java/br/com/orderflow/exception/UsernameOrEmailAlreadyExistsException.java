package br.com.orderflow.exception;

import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsernameOrEmailAlreadyExistsException extends RuntimeException {
    public UsernameOrEmailAlreadyExistsException(@Nullable String message) {
        super(message);
    }
}
