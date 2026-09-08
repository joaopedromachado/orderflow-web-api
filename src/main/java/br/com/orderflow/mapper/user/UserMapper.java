package br.com.orderflow.mapper.user;

import br.com.orderflow.api.controller.v1.auth.dto.request.RegisterUserRequest;
import br.com.orderflow.api.controller.v1.user.dto.request.UserUpdateRequest;
import br.com.orderflow.api.controller.v1.user.dto.response.UserResponse;
import br.com.orderflow.domain.user.User;
import br.com.orderflow.service.user.dto.UserUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserResponse toUserDTO(final User user) {
        return new UserResponse(user.getUserId().toString(), user.getUsername(), user.getEmail());
    }

    public static UserUpdateDTO toUserUpdateDTO(final UserUpdateRequest request) {
        return new UserUpdateDTO(request.username(), request.email());
    }

    public static User toUser(final RegisterUserRequest registerUserRequest) {
        return new User.Builder()
                .username(registerUserRequest.username())
                .email(registerUserRequest.email())
                .password(registerUserRequest.password())
                .build();
    }
}
