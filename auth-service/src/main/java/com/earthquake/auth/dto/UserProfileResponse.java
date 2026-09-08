package com.earthquake.auth.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserProfileResponse(
        Long id,
        String email,
        String username,
        String fullName,
        String status,
        Set<String> roles,
        LocalDateTime createdAt
) {
}
