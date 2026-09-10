package br.com.orderflow.client.exception;

import org.springframework.web.client.RestClientException;

public class AddressProviderUnavailableException extends RuntimeException {
    public AddressProviderUnavailableException(final String message) {
        super(message);
    }

    public AddressProviderUnavailableException(final String message, final RestClientException ex) {
        super(message, ex);
    }
}
