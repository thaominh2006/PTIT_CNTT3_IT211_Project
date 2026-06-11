package ra.edu.it211_project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import ra.edu.it211_project.dto.request.LoginRequest;
import ra.edu.it211_project.dto.request.RegisterRequest;
import ra.edu.it211_project.dto.response.AuthResponse;
import ra.edu.it211_project.dto.response.RegisterResponse;
import ra.edu.it211_project.entity.RoleEnum;
import ra.edu.it211_project.entity.User;
import ra.edu.it211_project.exception.DuplicateResourceException;
import ra.edu.it211_project.repository.TokenBlacklistRepository;
import ra.edu.it211_project.repository.UserRepository;
import ra.edu.it211_project.security.service.JwtService;
import ra.edu.it211_project.service.impl.AuthServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .passwordHash("hashedPassword")
                .email("test@gmail.com")
                .fullName("Test User")
                .role(RoleEnum.STUDENT)
                .isActive(true)
                .build();
    }


    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("123456");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(jwtService.generateAccessToken(mockUser)).thenReturn("access_token");
        when(jwtService.generateRefreshToken(mockUser)).thenReturn("refresh_token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("access_token", response.getAccessToken());
        assertEquals("refresh_token", response.getRefreshToken());
        assertEquals("STUDENT", response.getRole());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }


    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("123456");
        request.setEmail("newuser@gmail.com");
        request.setFullName("New User");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        RegisterResponse response = authService.register(request);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_DuplicateUsername_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("123456");
        request.setEmail("another@gmail.com");
        request.setFullName("Another User");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser2");
        request.setPassword("123456");
        request.setEmail("test@gmail.com");
        request.setFullName("New User 2");

        when(userRepository.existsByUsername("newuser2")).thenReturn(false);
        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_RoleIsAlwaysStudent() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newstudent");
        request.setPassword("123456");
        request.setEmail("newstudent@gmail.com");
        request.setFullName("New Student");

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertEquals(RoleEnum.STUDENT, user.getRole());
            return user;
        });

        authService.register(request);
        verify(userRepository).save(any(User.class));
    }
}