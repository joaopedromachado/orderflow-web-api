package br.com.orderflow.service.user;

import br.com.orderflow.domain.user.Role;
import br.com.orderflow.domain.user.User;
import br.com.orderflow.exception.UserNotFoundException;
import br.com.orderflow.exception.UsernameNotFoundException;
import br.com.orderflow.exception.UsernameOrEmailAlreadyExistsException;
import br.com.orderflow.api.controller.v1.user.dto.response.UserResponse;
import br.com.orderflow.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static br.com.orderflow.stub.UserStub.REGISTERED_USER_ID;
import static br.com.orderflow.stub.UserStub.REGISTER_USER_REQUEST;
import static br.com.orderflow.stub.UserStub.SIMPLE_USER;
import static br.com.orderflow.stub.UserStub.USER_UPDATE_DTO;
import static br.com.orderflow.stub.UserStub.USER_PAGE;
import static br.com.orderflow.stub.UserStub.basicRole;
import static br.com.orderflow.stub.UserStub.registeredUser;
import static br.com.orderflow.stub.UserStub.userToUpdate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Cadastro de usuário deve criar novo usuário quando solicitado dados corretamente.")
    void deveCriarNovoUsuario_quandoSolicitadoNovoCadastro() {
        final Role basicRole = basicRole();
        final User savedUser = registeredUser(basicRole);

        when(this.roleService.getRoleByName(Role.Values.BASIC.name())).thenReturn(basicRole);
        when(this.userRepository.existsUserByUsernameOrEmail(
                REGISTER_USER_REQUEST.username(),
                REGISTER_USER_REQUEST.email()
        )).thenReturn(false);
        when(this.userRepository.save(any(User.class))).thenReturn(savedUser);

        final var response = this.userService.register(REGISTER_USER_REQUEST);
        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(this.userRepository).existsUserByUsernameOrEmail(
                REGISTER_USER_REQUEST.username(),
                REGISTER_USER_REQUEST.email()
        );
        verify(this.roleService).getRoleByName(Role.Values.BASIC.name());
        verify(this.userRepository).save(userCaptor.capture());

        final User userToSave = userCaptor.getValue();

        assertThat(REGISTERED_USER_ID)
                .isEqualByComparingTo(response.userId());
        assertThat(REGISTER_USER_REQUEST.username())
                .isEqualTo(userToSave.getUsername());
        assertThat(REGISTER_USER_REQUEST.email())
                .isEqualTo(userToSave.getEmail());
        assertThat(REGISTER_USER_REQUEST.password())
                .isEqualTo(userToSave.getPassword());
        assertThat(1)
                .isEqualTo(userToSave.getRoles().size());
        assertThat(basicRole)
                .isSameAs(userToSave.getRoles().iterator().next());

        verify(roleService).getRoleByName(anyString());
        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("Deve retornar erro UsernameOrEmailAlreadyExistsException ao solicitar cadastro de usuário já existente.")
    void deveRetornarUsernameOrEmailAlreadyExistsException_quandoSolicitadoCadastroJaExistente() {
        final var basicRole = basicRole();

        when(this.roleService.getRoleByName(Role.Values.BASIC.name())).thenReturn(basicRole);
        when(this.userRepository.existsUserByUsernameOrEmail(
                REGISTER_USER_REQUEST.username(),
                REGISTER_USER_REQUEST.email()
        )).thenReturn(true);

        assertThatThrownBy(() -> this.userService.register(REGISTER_USER_REQUEST))
                .hasMessage("Já existe cadastro para nome de usuário ou email")
                .isInstanceOf(UsernameOrEmailAlreadyExistsException.class);

        verify(roleService).getRoleByName(anyString());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Deve retornar usuário quando solicitado username existente.")
    void deveRetornarUsuario_quandoSolicitadoUsername() {
        when(this.userRepository.findUserByUsernameIgnoreCase(SIMPLE_USER.getUsername()))
                .thenReturn(Optional.of(SIMPLE_USER));

        final User response = this.userService.getUserByUsername(SIMPLE_USER.getUsername());

        assertThat(response)
                .isNotNull()
                .extracting(User::getUserId, User::getUsername, User::getEmail)
                .containsExactly(
                        SIMPLE_USER.getUserId(),
                        SIMPLE_USER.getUsername(),
                        SIMPLE_USER.getEmail()
                );

        verify(this.userRepository).findUserByUsernameIgnoreCase(SIMPLE_USER.getUsername());
    }

    @Test
    @DisplayName("Deve retornar erro quando solicitado username inexistente.")
    void deveRetornarErro_quandoSolicitadoUsernameInexistente() {
        when(this.userRepository.findUserByUsernameIgnoreCase(SIMPLE_USER.getUsername()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.userService.getUserByUsername(SIMPLE_USER.getUsername()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Nome de usuário não foi encontrado.");

        verify(this.userRepository).findUserByUsernameIgnoreCase(SIMPLE_USER.getUsername());
    }

    @Test
    @DisplayName("Deve atualizar perfil quando solicitado pelo próprio usuário autenticado.")
    void deveAtualizarPerfil_quandoSolicitadoPeloProprioUsuarioAutenticado() {
        final User user = userToUpdate();

        when(this.userRepository.findById(REGISTERED_USER_ID)).thenReturn(Optional.of(user));
        when(this.userRepository.save(any(User.class))).thenReturn(user);

        final var response = this.userService.update(USER_UPDATE_DTO, REGISTERED_USER_ID.toString());
        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(this.userRepository).findById(REGISTERED_USER_ID);
        verify(this.userRepository).save(userCaptor.capture());

        final User userToSave = userCaptor.getValue();

        assertThat(userToSave)
                .isSameAs(user)
                .extracting(User::getUsername, User::getEmail)
                .containsExactly(USER_UPDATE_DTO.username(), USER_UPDATE_DTO.email());
        assertThat(userToSave.getUpdatedAt()).isNotNull();
        assertThat(response)
                .extracting("username", "email")
                .containsExactly(USER_UPDATE_DTO.username(), USER_UPDATE_DTO.email());
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar perfil de usuário inexistente.")
    void deveRetornarErro_quandoSolicitadaAtualizacaoDeUsuarioInexistente() {
        when(this.userRepository.findById(REGISTERED_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.userService.update(USER_UPDATE_DTO, REGISTERED_USER_ID.toString()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Usuário não foi encontrado ou não existe.");

        verify(this.userRepository).findById(REGISTERED_USER_ID);
        verify(this.userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve listar usuários quando solicitado.")
    void deveListarUsuarios_quandoSolicitado() {
        when(this.userRepository.findAll(any(Pageable.class))).thenReturn(USER_PAGE);

        final var response = this.userService.getUsers(0, 2);

        verify(this.userRepository).findAll(PageRequest.of(0, 2));

        assertThat(response)
                .singleElement()
                .extracting(UserResponse::userId, UserResponse::username, UserResponse::email)
                .containsExactly(
                        SIMPLE_USER.getUserId().toString(),
                        SIMPLE_USER.getUsername(),
                        SIMPLE_USER.getEmail()
                );
    }

    @Test
    @DisplayName("Deve excluir usuário quando solicitado id existente.")
    void deveExcluirUsuario_quandoSolicitadoUserId() {
        var userId = REGISTERED_USER_ID;

        doNothing().when(this.userRepository).deleteById(userId);

        userService.deleteUserById(userId.toString());

        verify(userRepository).deleteById(userId);

    }

    @Test
    @DisplayName("Deve retornar erro ao excluir usuário quando o repositório falhar.")
    void deveRetornarErro_quandoSolicitadoUserId() {
        var userId = REGISTERED_USER_ID;

        doThrow(new RuntimeException("Usuário não foi encontrado ou não existe")).when(this.userRepository).deleteById(userId);

        assertThatThrownBy(() -> this.userService.deleteUserById(userId.toString()))
                .hasMessage("Usuário não foi encontrado ou não existe")
                .isInstanceOf(UserNotFoundException.class);

        verify(this.userRepository).deleteById(userId);

    }
}
