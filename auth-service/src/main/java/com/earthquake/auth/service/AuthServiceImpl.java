package com.earthquake.auth.service;

import com.earthquake.auth.domain.Role;
import com.earthquake.auth.domain.User;
import com.earthquake.auth.dto.*;
import com.earthquake.auth.exception.DuplicateEmailException;
import com.earthquake.auth.exception.DuplicateUsernameException;
import com.earthquake.auth.exception.InvalidVerificationCodeException;
import com.earthquake.auth.rabbit.EmailVerificationMessage;
import com.earthquake.auth.rabbit.EmailVerificationProducer;
import com.earthquake.auth.repository.RoleRepository;
import com.earthquake.auth.security.JwtAuthenticationProvider;
import com.earthquake.auth.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
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
    private static final long OTP_EXPIRE_SECONDS = 600;
    private static final String PENDING_REGISTRATION_KEY_PREFIX = "pending_registration:";

    private final UserService userService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CacheService cacheService;
    private final EmailVerificationProducer emailVerificationProducer;

    public AuthServiceImpl(UserService userService,
                           JwtAuthenticationProvider jwtAuthenticationProvider,
                           PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository,
                           JwtTokenProvider jwtTokenProvider,
                           CacheService cacheService,
                           EmailVerificationProducer emailVerificationProducer) {
        this.userService = userService;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.cacheService = cacheService;
        this.emailVerificationProducer = emailVerificationProducer;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        Optional<User> user1 = userService.findByUsername(request.username());
        if (user1.isPresent()) {
            throw new DuplicateUsernameException(request.username());
        }
        Optional<User> user2 = userService.findByEmail(request.email());
        if (user2.isPresent()) {
            throw new DuplicateEmailException(request.email());
        }

        String otp = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));

        PendingRegistration pending = new PendingRegistration(
                request.username(),
                request.email(),
                request.fullName(),
                passwordEncoder.encode(request.password()),
                otp
        );

        // Cache first, publish second: the code must already be verifiable by the time
        // the mail carrying it can arrive.
        cacheService.put(
                PENDING_REGISTRATION_KEY_PREFIX + request.email(),
                pending,
                Duration.ofSeconds(OTP_EXPIRE_SECONDS)
        );

        emailVerificationProducer.publish(new EmailVerificationMessage(request.email(), otp));

        return new RegisterResponse(
                request.email(),
                "Verification code sent to email",
                OTP_EXPIRE_SECONDS
        );
    }

    @Override
    public AuthResponse verifyRegistration(VerifyRegistrationRequest request) {
        String cacheKey = PENDING_REGISTRATION_KEY_PREFIX + request.email();

        // Same exception whether the entry is missing/expired or the code is wrong:
        // telling them apart would reveal which emails have a registration in flight.
        PendingRegistration pending = cacheService.get(cacheKey, PendingRegistration.class)
                .orElseThrow(() -> new InvalidVerificationCodeException(request.email()));

        if (!codeMatches(pending.otp(), request.code())) {
            throw new InvalidVerificationCodeException(request.email());
        }

        // Re-check here too: the pending entry lives for 5 minutes, and someone else
        // could have taken the username or email in the meantime.
        if (userService.findByUsername(pending.username()).isPresent()) {
            throw new DuplicateUsernameException(pending.username());
        }
        if (userService.findByEmail(pending.email()).isPresent()) {
            throw new DuplicateEmailException(pending.email());
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not seeded"));

        User user = userService.saveUser(
                User.builder()
                        .username(pending.username())
                        .email(pending.email())
                        .fullName(pending.fullName())
                        .passwordHash(pending.passwordHash())
                        .roles(Set.of(userRole))
                        .build()
        );

        cacheService.delete(cacheKey);

        return issueTokens(user);
    }

    private AuthResponse issueTokens(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), roleNames);

        // Opaque refresh token: only Redis knows which user it belongs to, so it can
        // be revoked on logout, unlike a self-contained JWT.
        String refreshToken = UUID.randomUUID().toString();
        cacheService.put(REFRESH_TOKEN_KEY_PREFIX + refreshToken, user.getId(), REFRESH_TOKEN_TTL);

        return new AuthResponse(accessToken, refreshToken);
    }

    /** Constant-time compare so response timing can't be used to guess the code. */
    private boolean codeMatches(String expected, String provided) {
        if (expected == null || provided == null) {
            return false;
        }
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                provided.getBytes(StandardCharsets.UTF_8)
        );
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
