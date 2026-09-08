package com.earthquake.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
