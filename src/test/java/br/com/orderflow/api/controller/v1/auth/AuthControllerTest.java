package br.com.orderflow.api.controller.v1.auth;

import br.com.orderflow.api.controller.v1.auth.dto.request.RegisterUserRequest;
import br.com.orderflow.api.controller.v1.auth.dto.response.RegisterUserResponse;
import br.com.orderflow.domain.user.Role;
import br.com.orderflow.domain.user.User;
import br.com.orderflow.exception.GlobalExceptionHandler;
import br.com.orderflow.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static br.com.orderflow.stub.RoleStub.basicRole;
import static br.com.orderflow.stub.UserStub.ENCODED_PASSWORD;
import static br.com.orderflow.stub.UserStub.LOGIN_USER_REQUEST_JSON;
import static br.com.orderflow.stub.UserStub.REGISTERED_USER_ID;
import static br.com.orderflow.stub.UserStub.REGISTER_USER_REQUEST;
import static br.com.orderflow.stub.UserStub.REGISTER_USER_REQUEST_JSON;
import static br.com.orderflow.stub.UserStub.registeredUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private UserService userService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(
                        new AuthController(this.jwtEncoder, this.userService, this.passwordEncoder)
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve registrar usuário quando dados válidos forem enviados.")
    void deveRegistrarUsuario_quandoEnviadosDadosValidos() throws Exception {
        when(this.passwordEncoder.encode(REGISTER_USER_REQUEST.password())).thenReturn(ENCODED_PASSWORD);
        when(this.userService.register(new RegisterUserRequest(
                REGISTER_USER_REQUEST.username(),
                REGISTER_USER_REQUEST.email(),
                ENCODED_PASSWORD
        ))).thenReturn(new RegisterUserResponse(REGISTERED_USER_ID));

        this.mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_USER_REQUEST_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user_id").value(REGISTERED_USER_ID.toString()));

        verify(this.passwordEncoder).encode(REGISTER_USER_REQUEST.password());
        verify(this.userService).register(new RegisterUserRequest(
                REGISTER_USER_REQUEST.username(),
                REGISTER_USER_REQUEST.email(),
                ENCODED_PASSWORD
        ));
    }

    @Test
    @DisplayName("Deve retornar erro de validação quando cadastro não possuir senha.")
    void deveRetornarErroDeValidacao_quandoCadastroNaoPossuirSenha() throws Exception {
        this.mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"xpto","email":"xpto@gmail.com","password":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Deve retornar token quando credenciais forem válidas.")
    void deveRetornarToken_quandoCredenciaisForemValidas() throws Exception {
        final Role basicRole = basicRole();
        final User user = registeredUser(basicRole);
        final Jwt jwt = Jwt.withTokenValue("access-token")
                .header("alg", "Bcrypt")
                .claim("sub", REGISTERED_USER_ID.toString())
                .build();

        when(this.userService.getUserByUsername(REGISTER_USER_REQUEST.username())).thenReturn(user);
        when(this.passwordEncoder.matches(REGISTER_USER_REQUEST.password(), user.getPassword())).thenReturn(true);
        when(this.jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        this.mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_USER_REQUEST_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("access-token"))
                .andExpect(jsonPath("$.expire_in").value(300));

        verify(this.userService).getUserByUsername(REGISTER_USER_REQUEST.username());
        verify(this.jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    @DisplayName("Deve retornar credenciais inválidas quando senha não conferir.")
    void deveRetornarCredenciaisInvalidas_quandoSenhaNaoConferir() throws Exception {
        final User user = registeredUser(basicRole());

        when(this.userService.getUserByUsername(REGISTER_USER_REQUEST.username())).thenReturn(user);
        when(this.passwordEncoder.matches(REGISTER_USER_REQUEST.password(), user.getPassword())).thenReturn(false);

        this.mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_USER_REQUEST_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
