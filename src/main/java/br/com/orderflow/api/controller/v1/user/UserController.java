package br.com.orderflow.api.controller.v1.user;

import br.com.orderflow.service.user.UserService;
import br.com.orderflow.service.user.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsers(
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(this.userService.getUsers(page, size));
    }

}
