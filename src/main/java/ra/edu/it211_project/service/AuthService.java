package ra.edu.it211_project.service;

import ra.edu.it211_project.dto.request.ChangePasswordRequest;
import ra.edu.it211_project.dto.request.LoginRequest;
import ra.edu.it211_project.dto.request.RefreshTokenRequest;
import ra.edu.it211_project.dto.request.RegisterRequest;
import ra.edu.it211_project.dto.response.ApiResponse;
import ra.edu.it211_project.dto.response.AuthResponse;
import ra.edu.it211_project.dto.response.RegisterResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    RegisterResponse register(RegisterRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    ApiResponse<Void> logout(String token);
    ApiResponse<Void> changePassword(String username, ChangePasswordRequest request);
}