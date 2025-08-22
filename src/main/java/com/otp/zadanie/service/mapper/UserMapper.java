package com.otp.zadanie.service.mapper;

import com.otp.zadanie.domain.Role;
import com.otp.zadanie.domain.User;
import com.otp.zadanie.dto.CreateUserRequest;
import com.otp.zadanie.dto.UpdateUserRequest;
import com.otp.zadanie.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public Role newRole(String roleName) {
        return Role.builder()
                .roleName(roleName)
                .build();
    }

    public User toEntity(CreateUserRequest req, Role role) {
        return User.builder()
                .fio(req.fio())
                .phoneNumber(req.phoneNumber())
                .avatar(req.avatar())
                .role(role)
                .build();
    }

    public void updateEntity(User user, UpdateUserRequest req, Role role) {
        user.setFio(req.fio());
        user.setPhoneNumber(req.phoneNumber());
        user.setAvatar(req.avatar());
        user.setRole(role);
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFio(),
                user.getPhoneNumber(),
                user.getAvatar(),
                user.getRole() != null ? user.getRole().getRoleName() : null
        );
    }
}
