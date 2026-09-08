package com.earthquake.auth.service;

import com.earthquake.auth.domain.Role;
import com.earthquake.auth.domain.User;
import com.earthquake.auth.dto.*;
import com.earthquake.auth.exception.DuplicateEmailException;
import com.earthquake.auth.exception.DuplicateUsernameException;
import com.earthquake.auth.repository.RoleRepository;
import com.earthquake.auth.security.JwtAuthenticationProvider;
import com.earthquake.auth.security.JwtTokenProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);
    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh_token:";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final long OTP_EXPIRE_SECONDS = 300;

    private final UserService userService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public AuthServiceImpl(UserService userService,
                           JwtAuthenticationProvider jwtAuthenticationProvider,
                           PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository,
                           JwtTokenProvider jwtTokenProvider,
                           RedisTemplate<String, String> redisTemplate) {
        this.userService = userService;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String register(RegisterRequest request) {
        Optional<User> user1 = userService.findByUsername(request.username());
        if (user1.isPresent()) {
            throw new DuplicateUsernameException(request.username());
        }
        Optional<User> user2 = userService.findByEmail(request.email());
        if (user2.isPresent()) {
            throw new DuplicateEmailException(request.email());
        }

        int number = SECURE_RANDOM.nextInt(1_000_000);
        String otp = String.format("%06d", number);

        Instant expiresAt = Instant.now().plusSeconds(OTP_EXPIRE_SECONDS);

//        Role userRole = roleRepository.findByName("ROLE_USER")
//                .orElseThrow(() -> new IllegalStateException("ROLE_USER not seeded"));
//
//        User user = userService.saveUser(
//                User.builder()
//                        .username(request.username())
//                        .email(request.email())
//                        .passwordHash(passwordEncoder.encode(request.password()))
//                        .fullName(request.fullName())
//                        .roles(Set.of(userRole))
//                        .build()
//        );
//
//        Set<String> roleNames = user.getRoles().stream()
//                .map(Role::getName)
//                .collect(Collectors.toSet());
//        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), roleNames);
//
//        String refreshToken = UUID.randomUUID().toString();
//        redisTemplate.opsForValue().set(
//                REFRESH_TOKEN_KEY_PREFIX + refreshToken,
//                String.valueOf(user.getId()),
//                REFRESH_TOKEN_TTL
//        );

//        return new AuthResponse(accessToken, refreshToken);


    }

    @Override
    public AuthResponse verifyRegistration(VerifyRegistrationRequest request) {
        throw new UnsupportedOperationException("TODO: implement verifyRegistration");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        throw new UnsupportedOperationException("TODO: implement login");
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        throw new UnsupportedOperationException("TODO: implement refresh");
    }

    @Override
    public void logout(RefreshRequest request) {
        throw new UnsupportedOperationException("TODO: implement logout");
    }

    @Override
    public UserProfileResponse getProfile(String username) {
        throw new UnsupportedOperationException("TODO: implement getProfile");
    }

    @Override
    public UserProfileResponse updateProfile(String username, UpdateProfileRequest request) {
        throw new UnsupportedOperationException("TODO: implement updateProfile");
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest request) {
        throw new UnsupportedOperationException("TODO: implement changePassword");
    }

    @Override
    public List<UserProfileResponse> getAllUsers() {
        throw new UnsupportedOperationException("TODO: implement getAllUsers");
    }

    @Override
    public UserProfileResponse updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        throw new UnsupportedOperationException("TODO: implement updateUserStatus");
    }

    @Override
    public UserProfileResponse updateUserRoles(Long userId, UpdateUserRolesRequest request) {
        throw new UnsupportedOperationException("TODO: implement updateUserRoles");
    }
}
