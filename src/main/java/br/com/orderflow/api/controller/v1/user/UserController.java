package br.com.orderflow.api.controller.v1.user;

import br.com.orderflow.api.controller.v1.user.dto.request.AddressRequest;
import br.com.orderflow.api.controller.v1.user.dto.request.UserUpdateRequest;
import br.com.orderflow.api.controller.v1.user.dto.response.AddressResponse;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/me/address")
    public ResponseEntity<AddressResponse> registerAddress(@Valid @RequestBody final AddressRequest addressRequest,
                                                           final JwtAuthenticationToken jwtAuthenticationToken) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.userService.registerAddressOnUserProfile(addressRequest, jwtAuthenticationToken.getName()));
    }

    @PatchMapping("/me/address/{addressId}/default")
    public ResponseEntity<AddressResponse> changeDefaultAddress(
            @PathVariable final UUID addressId,
            final JwtAuthenticationToken jwtAuthenticationToken
    ) {
        return ResponseEntity.ok(this.userService.changeAndSetupDefaultAddress(
                addressId,
                jwtAuthenticationToken.getName()
        ));
    }

    @PutMapping("/me")
    public ResponseEntity<UserUpdateResponse> update(@Valid @RequestBody final UserUpdateRequest userUpdateRequest,
                                                     final JwtAuthenticationToken authenticationToken) {
        final UserUpdateDTO userDTO = UserMapper.toUserUpdateDTO(userUpdateRequest);
        return ResponseEntity.ok(this.userService.update(userDTO, authenticationToken.getName()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsers(
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(this.userService.getUsers(page, size));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable final String id) {
        this.userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
