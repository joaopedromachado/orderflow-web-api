package br.com.orderflow.api.controller.v1.user;

import br.com.orderflow.api.controller.v1.user.dto.response.AddressResponse;
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

import static br.com.orderflow.stub.AddressStub.ADDRESS_ID;
import static br.com.orderflow.stub.AddressStub.ADDRESS_REQUEST;
import static br.com.orderflow.stub.AddressStub.ADDRESS_REQUEST_JSON;
import static br.com.orderflow.stub.AddressStub.ADDRESS_RESPONSE;
import static br.com.orderflow.stub.UserStub.REGISTERED_USER_ID;
import static br.com.orderflow.stub.UserStub.SIMPLE_USER_RESPONSE;
import static br.com.orderflow.stub.UserStub.USER_UPDATE_DTO;
import static br.com.orderflow.stub.UserStub.USER_UPDATE_REQUEST_JSON;
import static br.com.orderflow.stub.UserStub.USER_UPDATE_RESPONSE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @DisplayName("Deve cadastrar endereço quando usuário autenticado solicitar dados válidos")
    void deveCadastrarEndereco_quandoSolicitoPorUsuarioAutenticado() throws Exception {
        when(this.userService.registerAddressOnUserProfile(
                ADDRESS_REQUEST,
                REGISTERED_USER_ID.toString()
        )).thenReturn(ADDRESS_RESPONSE);

        this.mockMvc.perform(
                post("/v1/users/me/address")
                        .principal(authenticationToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(ADDRESS_REQUEST_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.addressId").value(ADDRESS_RESPONSE.addressId().toString()))
                .andExpect(jsonPath("$.postalCode").value(ADDRESS_RESPONSE.postalCode()))
                .andExpect(jsonPath("$.street").value(ADDRESS_RESPONSE.street()))
                .andExpect(jsonPath("$.complement").value(ADDRESS_RESPONSE.complement()))
                .andExpect(jsonPath("$.neighborhood").value(ADDRESS_RESPONSE.neighborhood()))
                .andExpect(jsonPath("$.city").value(ADDRESS_RESPONSE.city()))
                .andExpect(jsonPath("$.state").value(ADDRESS_RESPONSE.state()))
                .andExpect(jsonPath("$.region").value(ADDRESS_RESPONSE.region()))
                .andExpect(jsonPath("$.number").value(ADDRESS_RESPONSE.number()))
                .andExpect(jsonPath("$.defaultAddress").value(ADDRESS_RESPONSE.defaultAddress()));

        verify(this.userService).registerAddressOnUserProfile(
                ADDRESS_REQUEST,
                REGISTERED_USER_ID.toString()
        );
    }

    @Test
    @DisplayName("Deve atualizar endereço padrão quando solicitado address_id válido")
    void deveAtualizarEnderecoPadrao_quandoSolicitadoAddressIdValido() throws Exception {
        final AddressResponse defaultAddressResponse = new AddressResponse(
                ADDRESS_RESPONSE.addressId(),
                ADDRESS_RESPONSE.postalCode(),
                ADDRESS_RESPONSE.street(),
                ADDRESS_RESPONSE.complement(),
                ADDRESS_RESPONSE.neighborhood(),
                ADDRESS_RESPONSE.city(),
                ADDRESS_RESPONSE.state(),
                ADDRESS_RESPONSE.region(),
                ADDRESS_RESPONSE.number(),
                true
        );

        when(this.userService.changeAndSetupDefaultAddress(
                ADDRESS_ID,
                REGISTERED_USER_ID.toString()
        )).thenReturn(defaultAddressResponse);

        this.mockMvc.perform(patch("/v1/users/me/address/{addressId}/default", ADDRESS_ID)
                        .principal(authenticationToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(defaultAddressResponse.addressId().toString()))
                .andExpect(jsonPath("$.postalCode").value(defaultAddressResponse.postalCode()))
                .andExpect(jsonPath("$.street").value(defaultAddressResponse.street()))
                .andExpect(jsonPath("$.complement").value(defaultAddressResponse.complement()))
                .andExpect(jsonPath("$.neighborhood").value(defaultAddressResponse.neighborhood()))
                .andExpect(jsonPath("$.city").value(defaultAddressResponse.city()))
                .andExpect(jsonPath("$.state").value(defaultAddressResponse.state()))
                .andExpect(jsonPath("$.region").value(defaultAddressResponse.region()))
                .andExpect(jsonPath("$.number").value(defaultAddressResponse.number()))
                .andExpect(jsonPath("$.defaultAddress").value(true));

        verify(this.userService).changeAndSetupDefaultAddress(
                ADDRESS_ID,
                REGISTERED_USER_ID.toString()
        );
    }

    @Test
    @DisplayName("Deve retornar usuários quando solicitada listagem paginada.")
    void deveRetornarUsuarios_quandoSolicitadaListagemPaginada() throws Exception {
        when(this.userService.getUsers(0, 20)).thenReturn(List.of(SIMPLE_USER_RESPONSE));

        this.mockMvc.perform(get("/v1/users/admin"))
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

        this.mockMvc.perform(put("/v1/users/me")
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
        this.mockMvc.perform(delete("/v1/users/admin/{id}", REGISTERED_USER_ID))
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
