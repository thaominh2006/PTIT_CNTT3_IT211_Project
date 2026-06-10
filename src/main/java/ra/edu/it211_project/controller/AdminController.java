package ra.edu.it211_project.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.edu.it211_project.dto.request.CourseRequest;
import ra.edu.it211_project.dto.request.UserUpdateRequest;
import ra.edu.it211_project.dto.response.ApiResponse;
import ra.edu.it211_project.dto.response.CourseResponse;
import ra.edu.it211_project.dto.response.UserResponse;
import ra.edu.it211_project.entity.RoleEnum;
import ra.edu.it211_project.service.CourseService;
import ra.edu.it211_project.service.UserService;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "UC-02: Quản trị Người dùng & Lớp học (chỉ ADMIN)")
public class AdminController {
    private final UserService userService;
    private final CourseService courseService;

    // ==================== USER MANAGEMENT ====================

    @GetMapping("/users")
    @Operation(summary = "Lấy danh sách người dùng (phân trang + tìm kiếm)")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) RoleEnum role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<UserResponse> users = userService.getAllUsers(search, role, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Lấy thông tin chi tiết người dùng theo ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Cập nhật thông tin người dùng (role, isActive, email...)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", userService.updateUser(id, request)));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Vô hiệu hóa tài khoản người dùng (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    // ==================== COURSE MANAGEMENT ====================

    @GetMapping("/courses")
    @Operation(summary = "Lấy danh sách khóa học (phân trang + tìm kiếm)")
    public ResponseEntity<ApiResponse<Page<CourseResponse>>> getAllCourses(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(courseService.getAllCourses(search, pageable)));
    }

    @GetMapping("/courses/{id}")
    @Operation(summary = "Lấy chi tiết khóa học theo ID")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCourseById(id)));
    }

    @PostMapping("/courses")
    @Operation(summary = "Tạo khóa học mới")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseRequest request) {
        CourseResponse created = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Course created successfully", created));
    }

    @PutMapping("/courses/{id}")
    @Operation(summary = "Cập nhật thông tin khóa học")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Course updated successfully", courseService.updateCourse(id, request)));
    }

    @DeleteMapping("/courses/{id}")
    @Operation(summary = "Xóa khóa học")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
