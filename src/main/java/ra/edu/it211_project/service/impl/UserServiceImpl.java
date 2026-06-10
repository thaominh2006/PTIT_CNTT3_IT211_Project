package ra.edu.it211_project.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.edu.it211_project.dto.request.UserUpdateRequest;
import ra.edu.it211_project.dto.response.ApiResponse;
import ra.edu.it211_project.dto.response.UserResponse;
import ra.edu.it211_project.entity.RoleEnum;
import ra.edu.it211_project.entity.User;
import ra.edu.it211_project.exception.ResourceNotFoundException;
import ra.edu.it211_project.repository.UserRepository;
import ra.edu.it211_project.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Page<UserResponse> getAllUsers(String search, RoleEnum role, Pageable pageable) {
        Page<User> users;
        String keyword = (search != null) ? search : "";

        if (role != null) {
            users = userRepository.findByRoleAndFullNameContainingIgnoreCase(role, keyword, pageable);
        } else {
            users = userRepository.findByFullNameContainingIgnoreCase(keyword, pageable);
        }

        // Using Java Stream API as required by UC-02
        return users.map(user -> UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getIsActive() != null) user.setIsActive(request.getIsActive());

        return mapToResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        // Soft delete - deactivate instead of hard delete
        user.setIsActive(false);
        userRepository.save(user);
        return ApiResponse.success("User deactivated successfully", null);
    }

    @Override
    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build();
    }
}
