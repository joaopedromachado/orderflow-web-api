package br.com.orderflow.service.user;

import br.com.orderflow.domain.user.Role;
import br.com.orderflow.domain.user.User;
import br.com.orderflow.exception.UsernameOrEmailAlreadyExistsException;
import br.com.orderflow.exception.UsernameNotFoundException;
import br.com.orderflow.mapper.user.UserMapper;
import br.com.orderflow.repository.user.UserRepository;
import br.com.orderflow.api.controller.v1.auth.dto.request.RegisterUserRequest;
import br.com.orderflow.api.controller.v1.auth.dto.response.RegisterUserResponse;
import br.com.orderflow.service.user.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final RoleService roleService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    public UserService(final RoleService roleService,
                       final UserRepository userRepository,
                       final ObjectMapper objectMapper) {
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public User getUserByUsername(final String username) {
        logger.debug("Buscando usuário por username={}", username);

        return this.userRepository.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nome de usuário não foi encontrado."));
    }

    private boolean verifyUsernameOrEmailExists(final String username,
                                                final String email) {
        final boolean userExists = userRepository.existsUserByUsernameOrEmail(username, email);
        logger.debug("Verificação de existência concluída para username={} e email={}: {}",
                username,
                email,
                userExists);
        return userExists;
    }

    public RegisterUserResponse register(final RegisterUserRequest registerUserRequest) {
        logger.info("Iniciando cadastro de usuário: username={}", registerUserRequest.username());

        final User user = objectMapper.convertValue(registerUserRequest, User.class);
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

    public List<UserDTO> getUsers(final int page,
                                  final int size) {
        final Page<User> users = this.userRepository.findAll(PageRequest.of(page, size));

        return users.stream()
                .map(UserMapper::toUser)
                .toList();
    }
}
