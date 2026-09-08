package com.earthquake.auth.service;

import com.earthquake.auth.dto.*;

import java.util.List;

/**
 * TODO: this whole service is a skeleton. Every method below needs a real
 * implementation (password hashing/verification, JWT issuance, refresh-token
 * rotation, login-audit writes, actual persistence logic, etc).
 */
public interface AuthService {

    String register(RegisterRequest request);

    AuthResponse verifyRegistration(VerifyRegistrationRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshRequest request);

    void logout(RefreshRequest request);

    UserProfileResponse getProfile(String username);

    UserProfileResponse updateProfile(String username, UpdateProfileRequest request);

    void changePassword(String username, ChangePasswordRequest request);

    List<UserProfileResponse> getAllUsers();

    UserProfileResponse updateUserStatus(Long userId, UpdateUserStatusRequest request);

    UserProfileResponse updateUserRoles(Long userId, UpdateUserRolesRequest request);
}
