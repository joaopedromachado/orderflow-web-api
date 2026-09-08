package br.com.orderflow.service.user;

import br.com.orderflow.api.controller.v1.auth.dto.request.RegisterUserRequest;
import br.com.orderflow.api.controller.v1.auth.dto.response.RegisterUserResponse;
import br.com.orderflow.api.controller.v1.user.dto.request.AddressRequest;
import br.com.orderflow.api.controller.v1.user.dto.response.AddressResponse;
import br.com.orderflow.api.controller.v1.user.dto.response.UserResponse;
import br.com.orderflow.api.controller.v1.user.dto.response.UserUpdateResponse;
import br.com.orderflow.client.ViaCepClient;
import br.com.orderflow.client.response.ViaCepResponse;
import br.com.orderflow.domain.user.Address;
import br.com.orderflow.domain.user.Role;
import br.com.orderflow.domain.user.User;
import br.com.orderflow.exception.UserNotFoundException;
import br.com.orderflow.exception.UsernameNotFoundException;
import br.com.orderflow.exception.UsernameOrEmailAlreadyExistsException;
import br.com.orderflow.mapper.user.AddressMapper;
import br.com.orderflow.mapper.user.UserMapper;
import br.com.orderflow.repository.user.UserRepository;
import br.com.orderflow.service.user.dto.UserUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final RoleService roleService;
    private final AddressService addressService;
    private final ViaCepClient viaCepClient;
    private final UserRepository userRepository;

    public UserService(
            final RoleService roleService,
            final AddressService addressService,
            final ViaCepClient viaCepClient,
            final UserRepository userRepository
    ) {
        this.roleService = roleService;
        this.addressService = addressService;
        this.viaCepClient = viaCepClient;
        this.userRepository = userRepository;
    }

    public User getUserByUsername(final String username) {
        logger.debug("Buscando usuário por username={}", username);

        return this.userRepository.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nome de usuário não foi encontrado."));
    }

    private User getUserById(final String id) {
        logger.debug("Buscando usuário por id={}", id);

        return this.userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFoundException("Usuário não foi encontrado ou não existe."));
    }

    private boolean verifyUsernameOrEmailExists(
            final String username,
            final String email) {
        final boolean userExists = userRepository.existsUserByUsernameOrEmail(username, email);
        logger.debug("Verificação de existência concluída para username={} e email={}: {}",
                username,
                email,
                userExists);
        return userExists;
    }

    @Transactional
    public RegisterUserResponse register(final RegisterUserRequest registerUserRequest) {
        logger.info("Iniciando cadastro de usuário: username={}", registerUserRequest.username());

        final User user = UserMapper.toUser(registerUserRequest);
        final Role role = this.roleService.getRoleByName(Role.Values.BASIC.name());

        user.setRoles(Set.of(role));

        if (this.verifyUsernameOrEmailExists(user.getUsername(), user.getEmail())) {
            logger.warn("Cadastro recusado: username={} ou email={} já está em uso",
                    registerUserRequest.username(),
                    registerUserRequest.email());
            throw new UsernameOrEmailAlreadyExistsException("Já existe cadastro para nome de usuário ou email");
        }

        final User userSaved = userRepository.save(user);

        logger.info("Usuário criado com sucesso: username={}, userId={}, role={}",
                userSaved.getUsername(),
                userSaved.getUserId(),
                userSaved.getRoles());

        return new RegisterUserResponse(userSaved.getUserId());
    }

    public AddressResponse registerAddressOnUserProfile(
            final AddressRequest addressRequest,
            final String token
    ) {
        final User user = this.getUserById(token);
        final ViaCepResponse viaCepResponse = this.viaCepClient.getAddressByCep(addressRequest.cep());
        final Address address = AddressMapper.toAddress(viaCepResponse, addressRequest, user);
        final Address addressSaved = this.addressService.saveAddress(address);

        logger.info("Endereço foi salvo com sucesso, addressId={}", addressSaved.getAddressId());

        return AddressMapper.toAddressResponse(addressSaved);
    }

    @Transactional
    public AddressResponse changeAndSetupDefaultAddress(
            final UUID addressId,
            final String token
    ) {
        final User user = this.getUserById(token);
        final Address defaultAddress = this.addressService.changeAndSetupDefaultAddress(addressId, user.getUserId());

        logger.info("Endereço padrão alterado com sucesso, userId={}, addressId={}",
                user.getUserId(), defaultAddress.getAddressId());

        return AddressMapper.toAddressResponse(defaultAddress);
    }

    public UserUpdateResponse update(
            final UserUpdateDTO userUpdateDTO,
            final String token
    ) {
        final User user = this.getUserById(token);

        user.setUsername(userUpdateDTO.username());
        user.setEmail(userUpdateDTO.email());
        user.setCurrentUpdated();

        final User userSaved = this.userRepository.save(user);

        return new UserUpdateResponse(userSaved.getUsername(), userSaved.getEmail());
    }

    public List<UserResponse> getUsers(
            final int page,
            final int size
    ) {
        final Page<User> users = this.userRepository.findAll(PageRequest.of(page, size));

        return users.stream()
                .map(UserMapper::toUserDTO)
                .toList();
    }

    public void deleteUserById(final String id) {
        logger.info("Iniciando remoção de usuário");
        try {
            this.userRepository.deleteById(UUID.fromString(id));
        } catch (Exception e) {
            logger.error("Erro ao remover usuário, error=", e.getCause());
            throw new UserNotFoundException("Usuário não foi encontrado ou não existe");
        }
        logger.info("Usuário removido com sucesso");
    }
}
