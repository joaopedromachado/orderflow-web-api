package br.com.orderflow.api.controller.v1.user.dto.response;

import java.util.UUID;

public record AddressResponse(
        UUID addressId,
        String postalCode,
        String street,
        String complement,
        String neighborhood,
        String city,
        String state,
        String region,
        String number,
        boolean defaultAddress
) {
}
