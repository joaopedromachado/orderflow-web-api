package br.com.orderflow.client.exception;

import org.springframework.web.client.RestClientException;

public class AddressProviderUnavailableException extends RuntimeException {
    public AddressProviderUnavailableException(String message) {
        super(message);
    }

    public AddressProviderUnavailableException(String message, RestClientException ex) {
        super(message, ex);
    }
}
