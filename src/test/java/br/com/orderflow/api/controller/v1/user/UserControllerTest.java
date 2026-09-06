package br.com.orderflow.api.controller.v1.user;

import br.com.orderflow.exception.GlobalExceptionHandler;
import br.com.orderflow.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static br.com.orderflow.stub.UserStub.REGISTERED_USER_ID;
import static br.com.orderflow.stub.UserStub.SIMPLE_USER_RESPONSE;
import static br.com.orderflow.stub.UserStub.USER_UPDATE_DTO;
import static br.com.orderflow.stub.UserStub.USER_UPDATE_REQUEST_JSON;
import static br.com.orderflow.stub.UserStub.USER_UPDATE_RESPONSE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController(this.userService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve retornar usuários quando solicitada listagem paginada.")
    void deveRetornarUsuarios_quandoSolicitadaListagemPaginada() throws Exception {
        when(this.userService.getUsers(0, 20)).thenReturn(List.of(SIMPLE_USER_RESPONSE));

        this.mockMvc.perform(get("/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(REGISTERED_USER_ID.toString()))
                .andExpect(jsonPath("$[0].username").value(SIMPLE_USER_RESPONSE.username()))
                .andExpect(jsonPath("$[0].email").value(SIMPLE_USER_RESPONSE.email()));

        verify(this.userService).getUsers(0, 20);
    }

    @Test
    @DisplayName("Deve atualizar perfil quando solicitação vier de usuário autenticado.")
    void deveAtualizarPerfil_quandoSolicitacaoVierDeUsuarioAutenticado() throws Exception {
        when(this.userService.update(USER_UPDATE_DTO, REGISTERED_USER_ID.toString()))
                .thenReturn(USER_UPDATE_RESPONSE);

        this.mockMvc.perform(put("/v1/users")
                        .principal(authenticationToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_UPDATE_REQUEST_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USER_UPDATE_RESPONSE.username()))
                .andExpect(jsonPath("$.email").value(USER_UPDATE_RESPONSE.email()));

        verify(this.userService).update(USER_UPDATE_DTO, REGISTERED_USER_ID.toString());
    }

    @Test
    @DisplayName("Deve excluir usuário quando solicitado id válido.")
    void deveExcluirUsuario_quandoSolicitadoIdValido() throws Exception {
        this.mockMvc.perform(delete("/v1/users/{id}", REGISTERED_USER_ID))
                .andExpect(status().isOk());

        verify(this.userService).deleteUserById(REGISTERED_USER_ID.toString());
    }

    private JwtAuthenticationToken authenticationToken() {
        final Instant issuedAt = Instant.now();
        final Jwt jwt = new Jwt(
                "access-token",
                issuedAt,
                issuedAt.plusSeconds(300),
                java.util.Map.of("alg", "BCRYPT"),
                java.util.Map.of("sub", REGISTERED_USER_ID.toString())
        );

        return new JwtAuthenticationToken(jwt);
    }
}
