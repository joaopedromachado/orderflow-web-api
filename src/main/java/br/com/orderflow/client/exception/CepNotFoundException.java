package br.com.orderflow.client.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CepNotFoundException extends RuntimeException {
    private static final String message = "CEP não encontrado.";
    public CepNotFoundException() {
        super(message);
    }
}
