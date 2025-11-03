package com.smartappointmentbooking.auth_service.service;

import com.smartappointmentbooking.auth_service.client.UserServiceClient;
import com.smartappointmentbooking.auth_service.dto.AuthResponse;
import com.smartappointmentbooking.auth_service.dto.LoginRequest;
import com.smartappointmentbooking.auth_service.dto.RegisterRequest;
import com.smartappointmentbooking.auth_service.dto.UserDTO;
import com.smartappointmentbooking.auth_service.entity.RefreshToken;
import com.smartappointmentbooking.auth_service.entity.Role;
import com.smartappointmentbooking.auth_service.entity.User;
import com.smartappointmentbooking.auth_service.exception.InvalidTokenException;
import com.smartappointmentbooking.auth_service.exception.ResourceNotFoundException;
import com.smartappointmentbooking.auth_service.exception.UserAlreadyExistsException;
import com.smartappointmentbooking.auth_service.repository.RefreshTokenRepository;
import com.smartappointmentbooking.auth_service.repository.RoleRepository;
import com.smartappointmentbooking.auth_service.repository.UserRepository;
import com.smartappointmentbooking.auth_service.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + registerRequest.getEmail() + " already exists");
        }

        Role role = roleRepository.findByName(Role.RoleEnum.valueOf(registerRequest.getRole()))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .emailVerified(false)

                .roles(Set.of(role))
                .build();

        user = userRepository.save(user);

        // Create user in user-service database as well
        userServiceClient.createUserInUserService(registerRequest, user.getId());

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        saveRefreshToken(user, refreshToken);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidTokenException("Invalid credentials");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        refreshTokenRepository.deleteByUserId(user.getId());
        saveRefreshToken(user, refreshToken);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    public AuthResponse refreshAccessToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new InvalidTokenException("Refresh token has expired");
        }

        User user = token.getUser();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        refreshTokenRepository.delete(token);
        saveRefreshToken(user, newRefreshToken);

        return buildAuthResponse(user, newAccessToken, newRefreshToken);
    }

    public UserDTO getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserDTO(user);
    }

    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationTime() / 1000)
                .user(mapToUserDTO(user))
                .build();
    }

    private UserDTO mapToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .emailVerified(user.getEmailVerified())
                .roles(user.getRoles().stream().map(r -> r.getName().toString()).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}
