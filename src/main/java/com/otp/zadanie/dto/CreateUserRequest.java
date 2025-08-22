package com.otp.zadanie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

@Builder
public record CreateUserRequest(
        @NotBlank @Size(max = 255) String fio,
        @NotBlank @Size(min = 7, max = 15) @Pattern(regexp = "\\+?\\d+", message = "Phone must contain only digits with optional leading +") String phoneNumber,
        @NotBlank @URL String avatar,
        @NotBlank @Size(max = 100) String role
) {}
