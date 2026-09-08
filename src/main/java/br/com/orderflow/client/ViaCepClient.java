package br.com.orderflow.client;

import br.com.orderflow.client.exception.AddressProviderUnavailableException;
import br.com.orderflow.client.exception.CepNotFoundException;
import br.com.orderflow.client.exception.InvalidCepException;
import br.com.orderflow.client.response.ViaCepResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class ViaCepClient {

    private static final Pattern CEP_PATTERN = Pattern.compile("\\d{5}-?\\d{3}");

    private final RestClient restClient;

    public ViaCepClient(@Qualifier("viaCepRestClient") final RestClient restClient) {
        this.restClient = restClient;
    }

    public ViaCepResponse getAddressByCep(final String cep) {
        final String normalizedCep = normalizeCep(cep);

        try {
            final ViaCepResponse response = restClient.get()
                    .uri("/{cep}/json/", normalizedCep)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, clientResponse) -> {
                        throw new AddressProviderUnavailableException(
                                "O serviço de endereço recusou a consulta."
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, clientResponse) -> {
                        throw new AddressProviderUnavailableException(
                                "O serviço de endereço está temporariamente indisponível."
                        );
                    })
                    .body(ViaCepResponse.class);

            if (Objects.isNull(response) || Boolean.TRUE.equals(response.erro())) {
                throw new CepNotFoundException();
            }

            return response;
        } catch (RestClientException exception) {
            throw new AddressProviderUnavailableException(
                    "Falha de comunicação ao consultar o serviço de endereço.",
                    exception
            );
        }
    }

    private String normalizeCep(final String cep) {
        if (Objects.isNull(cep) || !CEP_PATTERN.matcher(cep).matches()) {
            throw new InvalidCepException();
        }

        return cep.replace("-", "");
    }
}
