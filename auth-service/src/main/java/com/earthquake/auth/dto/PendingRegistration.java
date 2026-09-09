package com.earthquake.auth.dto;

/**
 * Internal model cached in Redis between /register and /register/verify.
 * Never return this from a controller — it holds the password hash and the OTP.
 */
public record PendingRegistration(
        String username,
        String email,
        String fullName,
        String passwordHash,
        String otp
) {
}
