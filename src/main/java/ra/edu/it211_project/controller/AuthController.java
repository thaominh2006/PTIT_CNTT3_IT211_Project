package ra.edu.it211_project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ra.edu.it211_project.dto.request.ChangePasswordRequest;
import ra.edu.it211_project.dto.request.LoginRequest;
import ra.edu.it211_project.dto.request.RefreshTokenRequest;
import ra.edu.it211_project.dto.request.RegisterRequest;
import ra.edu.it211_project.dto.response.ApiResponse;
import ra.edu.it211_project.dto.response.AuthResponse;
import ra.edu.it211_project.dto.response.RegisterResponse;
import ra.edu.it211_project.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login, Register, Refresh Token, Logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "FR-01: Đăng nhập & Cấp phát JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/register")
    @Operation(summary = "FR-04: Đăng ký tài khoản Sinh viên (không tạo token)")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "FR-02: Xoay vòng AccessToken bằng RefreshToken")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "FR-03: Đăng xuất & Thu hồi Token")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.substring(7);
        return ResponseEntity.ok(authService.logout(token));
    }

    @PutMapping("/change-password")
    @Operation(summary = "FR-10: Đổi mật khẩu")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(userDetails.getUsername(), request));
    }
}