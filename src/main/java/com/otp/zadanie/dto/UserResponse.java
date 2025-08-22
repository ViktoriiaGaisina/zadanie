package com.otp.zadanie.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse(
        UUID userID,
        String fio,
        String phoneNumber,
        String avatar,
        String role
) {}
