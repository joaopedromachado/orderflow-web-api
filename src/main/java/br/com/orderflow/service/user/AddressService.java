package br.com.orderflow.service.user;

import br.com.orderflow.domain.user.Address;
import br.com.orderflow.exception.AddressNotFoundException;
import br.com.orderflow.repository.user.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
public class AddressService {

    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);
    private final AddressRepository addressRepository;

    public AddressService(final AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Transactional
    public Address saveAddress(final Address address) {
        final String username = address.getUser().getUsername();
        try {
            logger.info("Iniciando tentativa para salvar endereço do usuário, username={}", username);
            return this.addressRepository.save(address);
        } catch (RuntimeException ex) {
            logger.error("Erro ao persistir dados do endereço, message={}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Transactional
    public Address changeAndSetupDefaultAddress(final UUID addressId, final UUID userId) {
        final Address address = this.addressRepository.findByAddressIdAndUserUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException("Endereço não encontrado para o usuário informado."));

        this.addressRepository.clearDefaultAddressForUser(userId);
        address.setDefaultAddress(true);

        return this.addressRepository.save(address);
    }
}
