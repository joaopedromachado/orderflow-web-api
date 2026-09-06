package br.com.orderflow.service.user;

import br.com.orderflow.domain.user.Role;
import br.com.orderflow.repository.user.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static br.com.orderflow.stub.RoleStub.basicRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    @DisplayName("Deve retornar role quando solicitada por nome existente.")
    void deveRetornarRole_quandoSolicitadoNomeExistente() {
        final Role role = basicRole();

        when(this.roleRepository.findRoleByName(Role.Values.BASIC.name())).thenReturn(Optional.of(role));

        final Role response = this.roleService.getRoleByName(Role.Values.BASIC.name());

        assertThat(response).isSameAs(role);
        verify(this.roleRepository).findRoleByName(Role.Values.BASIC.name());
    }

    @Test
    @DisplayName("Deve retornar erro quando solicitada role inexistente.")
    void deveRetornarErro_quandoSolicitadoNomeInexistente() {
        when(this.roleRepository.findRoleByName(Role.Values.BASIC.name())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.roleService.getRoleByName(Role.Values.BASIC.name()))
                .isInstanceOf(NoSuchElementException.class);

        verify(this.roleRepository).findRoleByName(Role.Values.BASIC.name());
    }
}
