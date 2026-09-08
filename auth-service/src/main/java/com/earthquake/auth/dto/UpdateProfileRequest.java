package com.earthquake.auth.dto;

import jakarta.validation.constraints.Email;

public record UpdateProfileRequest(
        @Email String email,
        String fullName
) {
}
