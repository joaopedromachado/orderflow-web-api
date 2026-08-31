package br.com.orderflow.api.controller;

import br.com.orderflow.api.controller.dto.request.LoginRequest;
import br.com.orderflow.api.controller.dto.response.LoginResponse;
import br.com.orderflow.domain.security.User;
import br.com.orderflow.service.UserService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@Validated
public class TokenController {

    private static Long EXPIRES_IN = 300L;

    private final JwtEncoder jwtEncoder;
    private final UserService userService;
    private BCryptPasswordEncoder passwordEncoder;

    public TokenController(JwtEncoder jwtEncoder,
                           UserService userService,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
        this.passwordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        final User user = this.userService.getUserByUsername(loginRequest.username());

        if (user.getUsername().isEmpty() || !user.isValidLogin(loginRequest, passwordEncoder)) {
            throw new BadCredentialsException("Nome de usuário ou senha são inválidos");
        }

        final Instant now = Instant.now();

        final JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("orderflow-web-api")
                .subject(user.getUserId().toString())
                .expiresAt(now.plusSeconds(EXPIRES_IN))
                .issuedAt(now)
                .build();

        final Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims));

        return ResponseEntity.ok().body(new LoginResponse(jwt.getTokenValue(), EXPIRES_IN));
    }

}
