package br.com.orderflow.api.controller.v1.user;

import br.com.orderflow.api.controller.v1.user.dto.request.UserUpdateRequest;
import br.com.orderflow.api.controller.v1.user.dto.response.UserUpdateResponse;
import br.com.orderflow.mapper.user.UserMapper;
import br.com.orderflow.service.user.UserService;
import br.com.orderflow.api.controller.v1.user.dto.response.UserResponse;
import br.com.orderflow.service.user.dto.UserUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PutMapping
    public ResponseEntity<UserUpdateResponse> update(@Valid @RequestBody final UserUpdateRequest userUpdateRequest,
                                                     final JwtAuthenticationToken authenticationToken) {
        final UserUpdateDTO userDTO = UserMapper.toUserUpdateDTO(userUpdateRequest);
        return ResponseEntity.ok(this.userService.update(userDTO, authenticationToken.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsers(
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(this.userService.getUsers(page, size));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable final String id) {
        this.userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
