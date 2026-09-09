package br.com.orderflow.service.user;

import br.com.orderflow.domain.user.Address;
import br.com.orderflow.repository.user.AddressRepository;
import br.com.orderflow.stub.AddressStub;
import br.com.orderflow.stub.UserStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static br.com.orderflow.stub.AddressStub.ADDRESS_ID;
import static br.com.orderflow.stub.AddressStub.SIMPLE_ADDRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    @Test
    @DisplayName("Deve salvar endereço quando solicitado dados válidos.")
    void deveSalvarAddress_quandoSolicitadoDadosValidos() {
        final var user = UserStub.SIMPLE_USER;
        final var address = AddressStub.savedAddress(user);
        when(this.addressRepository.save(any()))
                .thenReturn(address);

        final var response = this.addressService.saveAddress(address);

        assertThat(response)
                .extracting(Address::getAddressId, Address::getUser)
                .containsExactly(SIMPLE_ADDRESS.getAddressId(), SIMPLE_ADDRESS.getUser());

        verify(this.addressRepository).save(any());
    }

    @Test
    @DisplayName("Deve alterar e definir o endereço padrão.")
    void deveAlterarEndereco_quandoDefinidoEnderecoPorPadrao() {
        final Address address = AddressStub.savedAddress(UserStub.SIMPLE_USER);

        when(this.addressRepository.findByAddressIdAndUserUserId(ADDRESS_ID, UserStub.REGISTERED_USER_ID))
                .thenReturn(Optional.of(address));
        when(this.addressRepository.save(address)).thenReturn(address);

        final Address response = this.addressService.changeAndSetupDefaultAddress(
                ADDRESS_ID,
                UserStub.REGISTERED_USER_ID
        );

        assertThat(response)
                .isSameAs(address)
                .extracting(Address::isDefaultAddress)
                .isEqualTo(true);

        verify(this.addressRepository).clearDefaultAddressForUser(UserStub.REGISTERED_USER_ID);
        verify(this.addressRepository).save(address);
    }
}
