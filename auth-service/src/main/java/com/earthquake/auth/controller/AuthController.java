package com.earthquake.auth.controller;

import com.earthquake.auth.dto.*;
import com.earthquake.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/register/verify")
    public AuthResponse verifyRegistration(@Valid @RequestBody VerifyRegistrationRequest request) {
        return authService.verifyRegistration(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public void logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request);
    }

    @GetMapping("/me")
    public UserProfileResponse getProfile(Principal principal) {
        return authService.getProfile(principal.getName());
    }

    @PutMapping("/me")
    public UserProfileResponse updateProfile(Principal principal, @Valid @RequestBody UpdateProfileRequest request) {
        return authService.updateProfile(principal.getName(), request);
    }

    @PutMapping("/me/password")
    public void changePassword(Principal principal, @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(principal.getName(), request);
    }

    @GetMapping("/admin/users")
    public List<UserProfileResponse> getAllUsers() {
        return authService.getAllUsers();
    }

    @PutMapping("/admin/users/{id}/status")
    public UserProfileResponse updateUserStatus(@PathVariable Long id, @Valid @RequestBody UpdateUserStatusRequest request) {
        return authService.updateUserStatus(id, request);
    }

    @PutMapping("/admin/users/{id}/roles")
    public UserProfileResponse updateUserRoles(@PathVariable Long id, @Valid @RequestBody UpdateUserRolesRequest request) {
        return authService.updateUserRoles(id, request);
    }
}
