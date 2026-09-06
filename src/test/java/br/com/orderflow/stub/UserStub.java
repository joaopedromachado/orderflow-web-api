package br.com.orderflow.stub;

import br.com.orderflow.api.controller.v1.auth.dto.request.RegisterUserRequest;
import br.com.orderflow.api.controller.v1.user.dto.request.UserUpdateRequest;
import br.com.orderflow.api.controller.v1.user.dto.response.UserResponse;
import br.com.orderflow.api.controller.v1.user.dto.response.UserUpdateResponse;
import br.com.orderflow.domain.user.Role;
import br.com.orderflow.domain.user.User;
import br.com.orderflow.service.user.dto.UserUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class UserStub {

    public static final UUID REGISTERED_USER_ID = UUID.fromString("378b5ce6-7a2b-4600-b94a-54f624cd4fb2");
    public static final User SIMPLE_USER = new User.Builder()
            .userId(REGISTERED_USER_ID)
            .username("user_xpto")
            .email("user.xpto@gmail.com")
            .password("xpto@123")
            .build();
    public static final RegisterUserRequest REGISTER_USER_REQUEST = new RegisterUserRequest(
            "xpto",
            "xpto@gmail.com",
            "senhaCodificada"
    );
    public static final String ENCODED_PASSWORD = "$2a$10$encodedPasswordForTests";
    public static final String REGISTER_USER_REQUEST_JSON = """
            {"username":"%s","email":"%s","password":"%s"}
            """.formatted(
            REGISTER_USER_REQUEST.username(),
            REGISTER_USER_REQUEST.email(),
            REGISTER_USER_REQUEST.password()
    );
    public static final String LOGIN_USER_REQUEST_JSON = """
            {"username":"%s","password":"%s"}
            """.formatted(REGISTER_USER_REQUEST.username(), REGISTER_USER_REQUEST.password());
    public static final UserUpdateRequest USER_UPDATE_REQUEST = new UserUpdateRequest(
            "updated_user",
            "updated.user@gmail.com"
    );
    public static final String USER_UPDATE_REQUEST_JSON = """
            {"username":"%s","email":"%s"}
            """.formatted(USER_UPDATE_REQUEST.username(), USER_UPDATE_REQUEST.email());

    public static final Page<User> USER_PAGE = new PageImpl<>(
            List.of(SIMPLE_USER),
            PageRequest.of(0, 2),
            1
    );
    public static final UserUpdateDTO USER_UPDATE_DTO = new UserUpdateDTO(
            "updated_user",
            "updated.user@gmail.com"
    );
    public static final UserResponse SIMPLE_USER_RESPONSE = new UserResponse(
            REGISTERED_USER_ID.toString(),
            SIMPLE_USER.getUsername(),
            SIMPLE_USER.getEmail()
    );
    public static final UserUpdateResponse USER_UPDATE_RESPONSE = new UserUpdateResponse(
            USER_UPDATE_DTO.username(),
            USER_UPDATE_DTO.email()
    );

    private UserStub() {
    }

    public static Role basicRole() {
        final Role role = new Role();
        role.setName(Role.Values.BASIC.name());
        return role;
    }

    public static User userToUpdate() {
        return new User.Builder()
                .userId(REGISTERED_USER_ID)
                .username(SIMPLE_USER.getUsername())
                .email(SIMPLE_USER.getEmail())
                .password(SIMPLE_USER.getPassword())
                .build();
    }

    public static User registeredUser(final Role role) {
        return new User.Builder()
                .userId(REGISTERED_USER_ID)
                .username(REGISTER_USER_REQUEST.username())
                .email(REGISTER_USER_REQUEST.email())
                .password(REGISTER_USER_REQUEST.password())
                .roles(Set.of(role))
                .build();
    }

}
