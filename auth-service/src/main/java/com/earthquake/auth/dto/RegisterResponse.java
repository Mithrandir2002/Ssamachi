package com.earthquake.auth.dto;

public record RegisterResponse(
        String email,
        String message,
        long expiresInSeconds
) {
}
