package br.com.orderflow.api.controller;

import br.com.orderflow.api.controller.dto.request.CreateUserRequest;
import br.com.orderflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static br.com.orderflow.api.controller.UserController.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH)
@Validated
public class UserController {

    static final String BASE_PATH = "/users";

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(final UserService userService,
                          final BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth")
    public void createUser(@Valid @RequestBody final CreateUserRequest createUserRequest) {
        userService.createUser(
                new CreateUserRequest(
                        createUserRequest.username(),
                        createUserRequest.email(),
                        passwordEncoder.encode(createUserRequest.password())
                )
        );
    }

}
