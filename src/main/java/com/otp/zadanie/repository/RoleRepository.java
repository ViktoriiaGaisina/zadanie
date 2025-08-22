package com.otp.zadanie.repository;

import com.otp.zadanie.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByRoleNameIgnoreCase(String roleName);
}
