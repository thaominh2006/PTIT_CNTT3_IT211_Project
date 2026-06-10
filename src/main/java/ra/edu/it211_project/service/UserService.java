package ra.edu.it211_project.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.edu.it211_project.dto.request.UserUpdateRequest;
import ra.edu.it211_project.dto.response.ApiResponse;
import ra.edu.it211_project.dto.response.UserResponse;
import ra.edu.it211_project.entity.RoleEnum;

public interface UserService {
    Page<UserResponse> getAllUsers(String search, RoleEnum role, Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    ApiResponse<Void> deleteUser(Long id);
    UserResponse getCurrentUser(String username);
}
