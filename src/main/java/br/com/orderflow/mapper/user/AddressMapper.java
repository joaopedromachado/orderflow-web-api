package br.com.orderflow.mapper.user;

import br.com.orderflow.api.controller.v1.user.dto.request.AddressRequest;
import br.com.orderflow.api.controller.v1.user.dto.response.AddressResponse;
import br.com.orderflow.client.response.ViaCepResponse;
import br.com.orderflow.domain.user.Address;
import br.com.orderflow.domain.user.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public static Address toAddress(final ViaCepResponse viaCepResponse,
                                    final AddressRequest request,
                                    final User user) {
        return new Address.Builder()
                .user(user)
                .postalCode(viaCepResponse.cep())
                .street(viaCepResponse.logradouro())
                .complement(StringUtils.defaultIfBlank(request.complement(), viaCepResponse.complemento()))
                .neighborhood(viaCepResponse.bairro())
                .city(viaCepResponse.localidade())
                .state(viaCepResponse.uf())
                .region(viaCepResponse.estado())
                .number(request.number())
                .build();
    }

    public static AddressResponse toAddressResponse(final Address address) {
        return new AddressResponse(
                address.getAddressId(),
                address.getPostalCode(),
                address.getStreet(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getRegion(),
                address.getNumber(),
                address.isDefaultAddress());
    }

}
