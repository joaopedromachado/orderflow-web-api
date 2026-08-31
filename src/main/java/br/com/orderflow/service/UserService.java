package br.com.orderflow.service;

import br.com.orderflow.api.controller.dto.request.CreateUserRequest;
import br.com.orderflow.domain.security.Role;
import br.com.orderflow.domain.security.User;
import br.com.orderflow.exception.UserAlreadyExistsException;
import br.com.orderflow.exception.UsernameNotFoundException;
import br.com.orderflow.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

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

    public void createUser(final CreateUserRequest createUserRequest) {
        logger.info("Iniciando cadastro de usuário: username={}", createUserRequest.username());

        final User user = objectMapper.convertValue(createUserRequest, User.class);
        final Role role = this.roleService.getRoleByName(Role.Values.BASIC.name());

        user.setRoles(Set.of(role));

        if (this.verifyUsernameOrEmailExists(user.getUsername(), createUserRequest.email())) {
            logger.warn("Cadastro recusado: username={} ou email={} já está em uso",
                    createUserRequest.username(),
                    createUserRequest.email());
            throw new UserAlreadyExistsException("Usuário já existente");
        }

        final User userSaved = userRepository.save(user);

        logger.info("Usuário criado com sucesso: username={}, userId={}, role={}",
                userSaved.getUsername(),
                userSaved.getUserId(),
                role.getName());
    }
}
