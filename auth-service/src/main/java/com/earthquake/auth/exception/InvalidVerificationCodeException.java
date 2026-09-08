package com.earthquake.auth.exception;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException(String email) {
        super("Invalid or expired verification code for email: " + email);
    }
}
