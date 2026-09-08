package br.com.orderflow.stub;

import br.com.orderflow.api.controller.v1.user.dto.request.AddressRequest;
import br.com.orderflow.api.controller.v1.user.dto.response.AddressResponse;
import br.com.orderflow.client.response.ViaCepResponse;
import br.com.orderflow.domain.user.Address;
import br.com.orderflow.domain.user.User;

import java.util.UUID;

import static br.com.orderflow.stub.UserStub.*;

public final class AddressStub {

    public static final UUID ADDRESS_ID = UUID.fromString("791034c6-7533-4e47-825a-5526236bbd1c");
    public static final AddressRequest ADDRESS_REQUEST = new AddressRequest("01001-000", "100", "Apto 10");
    public static final String ADDRESS_REQUEST_JSON = """
            {"cep":"01001-000","number":"100","complement":"Apto 10"}
            """;
    public static final ViaCepResponse VIA_CEP_RESPONSE = new ViaCepResponse(
            "01001-000",
            "Praça da Sé",
            "lado ímpar",
            "Sé",
            "São Paulo",
            "SP",
            "São Paulo",
            false
    );
    public static final AddressResponse ADDRESS_RESPONSE = new AddressResponse(
            ADDRESS_ID,
            VIA_CEP_RESPONSE.cep(),
            VIA_CEP_RESPONSE.logradouro(),
            ADDRESS_REQUEST.complement(),
            VIA_CEP_RESPONSE.bairro(),
            VIA_CEP_RESPONSE.localidade(),
            VIA_CEP_RESPONSE.uf(),
            VIA_CEP_RESPONSE.estado(),
            ADDRESS_REQUEST.number(),
            false
    );

    public static final Address SIMPLE_ADDRESS = new Address.Builder()
            .addressId(ADDRESS_ID)
            .user(SIMPLE_USER)
            .postalCode(VIA_CEP_RESPONSE.cep())
            .street(VIA_CEP_RESPONSE.logradouro())
            .complement(ADDRESS_REQUEST.complement())
            .neighborhood(VIA_CEP_RESPONSE.bairro())
            .city(VIA_CEP_RESPONSE.localidade())
            .state(VIA_CEP_RESPONSE.uf())
            .region(VIA_CEP_RESPONSE.estado())
            .number(ADDRESS_REQUEST.number())
            .defaultAddress(true)
            .build();

    private AddressStub() {
    }

    public static Address savedAddress(final User user) {
        return new Address.Builder()
                .addressId(ADDRESS_ID)
                .user(user)
                .postalCode(VIA_CEP_RESPONSE.cep())
                .street(VIA_CEP_RESPONSE.logradouro())
                .complement(ADDRESS_REQUEST.complement())
                .neighborhood(VIA_CEP_RESPONSE.bairro())
                .city(VIA_CEP_RESPONSE.localidade())
                .state(VIA_CEP_RESPONSE.uf())
                .region(VIA_CEP_RESPONSE.estado())
                .number(ADDRESS_REQUEST.number())
                .build();
    }
}
