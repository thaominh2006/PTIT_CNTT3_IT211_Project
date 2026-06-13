package ra.edu.it211_project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.edu.it211_project.dto.request.ChangePasswordRequest;
import ra.edu.it211_project.dto.request.LoginRequest;
import ra.edu.it211_project.dto.request.RefreshTokenRequest;
import ra.edu.it211_project.dto.request.RegisterRequest;
import ra.edu.it211_project.dto.response.ApiResponse;
import ra.edu.it211_project.dto.response.AuthResponse;
import ra.edu.it211_project.dto.response.RegisterResponse;
import ra.edu.it211_project.entity.RoleEnum;
import ra.edu.it211_project.entity.User;
import ra.edu.it211_project.exception.DuplicateResourceException;
import ra.edu.it211_project.exception.InvalidStateException;
import ra.edu.it211_project.exception.ResourceNotFoundException;
import ra.edu.it211_project.repository.UserRepository;
import ra.edu.it211_project.security.service.JwtService;
import ra.edu.it211_project.security.service.RedisTokenBlacklistService;
import ra.edu.it211_project.service.AuthService;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RedisTokenBlacklistService redisTokenBlacklistService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("User '{}' with role '{}' logged in successfully", user.getUsername(), user.getRole());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .role(user.getRole().name())
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(RoleEnum.STUDENT)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);

        log.info("New STUDENT registered: {}", saved.getUsername());

        return RegisterResponse.builder()
                .userId(saved.getId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .fullName(saved.getFullName())
                .role(saved.getRole())
                .message("Registration successful. Please login to get your token.")
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (jwtService.isTokenExpired(refreshToken)) {
            throw new InvalidStateException("Refresh token has expired. Please login again.");
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .role(user.getRole().name())
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }

    /**
     * FR-13-AF3: Logout sử dụng Redis Blacklist thay vì lưu vào Database.
     * - Key = token, TTL = thời gian còn lại đến khi token hết hạn tự nhiên.
     * - Redis tự động xóa key khi TTL về 0, tránh phình bảng DB và tắc nghẽn truy vấn.
     */
    @Override
    public ApiResponse<Void> logout(String token) {
        String username = jwtService.extractUsername(token);
        userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Date expiration = jwtService.extractExpiration(token);
        long remainingMillis = expiration.getTime() - System.currentTimeMillis();

        redisTokenBlacklistService.blacklistToken(token, remainingMillis);
        log.info("Token revoked (Redis) for user: {}", username);

        return ApiResponse.success("Logged out successfully", null);
    }

    @Override
    @Transactional
    public ApiResponse<Void> changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidStateException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed for user: {}", username);
        return ApiResponse.success("Password changed successfully", null);
    }
}