package com.otp.zadanie.service;

import com.otp.zadanie.domain.Role;
import com.otp.zadanie.domain.User;
import com.otp.zadanie.dto.CreateUserRequest;
import com.otp.zadanie.dto.UpdateUserRequest;
import com.otp.zadanie.dto.UserResponse;
import com.otp.zadanie.repository.RoleRepository;
import com.otp.zadanie.repository.UserRepository;
import com.otp.zadanie.service.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse createUser(@NotNull @Valid CreateUserRequest req) {
        Role role = roleRepository.findByRoleNameIgnoreCase(req.role())
                .orElseGet(() -> roleRepository.save(userMapper.newRole(req.role())));
        User user = userMapper.toEntity(req, role);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Cacheable(cacheNames = "users", key = "#userId")
    public UserResponse getUser(@NotNull UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        return userMapper.toResponse(user);
    }

    @CachePut(cacheNames = "users", key = "#req.userID()")
    @Transactional
    public UserResponse updateUser(@NotNull @Valid UpdateUserRequest req) {
        User user = userRepository.findById(req.userID())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + req.userID()));
        Role role = roleRepository.findByRoleNameIgnoreCase(req.role())
                .orElseGet(() -> roleRepository.save(userMapper.newRole(req.role())));
        userMapper.updateEntity(user, req, role);
        return userMapper.toResponse(user);
    }

    @CacheEvict(cacheNames = "users", key = "#userId")
    @Transactional
    public void deleteUser(@NotNull UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        Role role = user.getRole();
        userRepository.delete(user);
        if (role != null) {
            long refs = userRepository.countByRole_Id(role.getId());
            if (refs == 0) {
                roleRepository.delete(role);
            }
        }
    }
}
