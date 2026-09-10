package br.com.orderflow.client.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCepException extends RuntimeException {
    private static final String MESSAGE = "CEP inválido. Informe 8 dígitos.";

    public InvalidCepException() {
        super(MESSAGE);
    }
}
