package br.com.orderflow.api.controller.v1.auth;

import br.com.orderflow.api.controller.v1.auth.dto.request.LoginUserRequest;
import br.com.orderflow.api.controller.v1.auth.dto.request.RegisterUserRequest;
import br.com.orderflow.api.controller.v1.auth.dto.response.LoginUserResponse;
import br.com.orderflow.api.controller.v1.auth.dto.response.RegisterUserResponse;
import br.com.orderflow.domain.user.Role;
import br.com.orderflow.domain.user.User;
import br.com.orderflow.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/auth")
@Validated
public class AuthController {

    private static Long EXPIRES_IN = 300L;

    private final JwtEncoder jwtEncoder;
    private final UserService userService;
    private BCryptPasswordEncoder passwordEncoder;

    public AuthController(final JwtEncoder jwtEncoder,
                          final UserService userService,
                          final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
        this.passwordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(@Valid @RequestBody final LoginUserRequest loginUserRequest) {
        final User user = this.userService.getUserByUsername(loginUserRequest.username());

        if (user.getUsername().isEmpty() || !user.isValidLogin(loginUserRequest, passwordEncoder)) {
            throw new BadCredentialsException("Nome de usuário ou senha são inválidos");
        }

        final Instant now = Instant.now();

        String scopes = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        final JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("orderflow-web-api")
                .subject(user.getUserId().toString())
                .expiresAt(now.plusSeconds(EXPIRES_IN))
                .issuedAt(now)
                .claim("scope", scopes)
                .build();

        final Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims));

        return ResponseEntity.ok().body(new LoginUserResponse(jwt.getTokenValue(), EXPIRES_IN));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody final RegisterUserRequest registerUserRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                userService.register(
                        new RegisterUserRequest(
                                registerUserRequest.username(),
                                registerUserRequest.email(),
                                passwordEncoder.encode(registerUserRequest.password())
                        )
                )
        );
    }


}
