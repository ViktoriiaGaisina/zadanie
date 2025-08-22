package com.otp.zadanie.repository;

import com.otp.zadanie.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    long countByRole_Id(UUID roleId);
}
