package ra.edu.it211_project.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.it211_project.dto.request.SubmissionRequest;
import ra.edu.it211_project.dto.response.ApiResponse;
import ra.edu.it211_project.dto.response.CourseResponse;
import ra.edu.it211_project.dto.response.MaterialResponse;
import ra.edu.it211_project.dto.response.SubmissionResponse;
import ra.edu.it211_project.service.CourseService;
import ra.edu.it211_project.service.MaterialService;
import ra.edu.it211_project.service.SubmissionService;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
@Tag(name = "Student", description = "FR-06: Đăng ký khóa học | FR-07: Nộp bài đồ án | UC-05: Upload báo cáo")
public class StudentController {
    private final CourseService courseService;
    private final SubmissionService submissionService;
    private final MaterialService materialService;

    // ==================== COURSES ====================

    @GetMapping("/courses")
    @Operation(summary = "Xem tất cả khóa học có thể đăng ký (phân trang)")
    public ResponseEntity<ApiResponse<Page<CourseResponse>>> getAllCourses(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(courseService.getAllCourses(search, pageable)));
    }

    @PostMapping("/courses/{courseId}/enroll")
    @Operation(summary = "FR-06: Đăng ký tham gia khóa học")
    public ResponseEntity<ApiResponse<Void>> enrollCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        ApiResponse<Void> response = courseService.enrollStudent(courseId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/courses/enrolled")
    @Operation(summary = "Xem danh sách khóa học đã đăng ký")
    public ResponseEntity<ApiResponse<Page<CourseResponse>>> getEnrolledCourses(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(courseService.getEnrolledCourses(userDetails.getUsername(), pageable)));
    }

    // ==================== SUBMISSIONS ====================

    @PostMapping("/submissions")
    @Operation(summary = "FR-07: Nộp bài đồ án (link GitHub)")
    public ResponseEntity<ApiResponse<SubmissionResponse>> submitAssignment(
            @Valid @RequestBody SubmissionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        SubmissionResponse response = submissionService.submitAssignment(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Assignment submitted successfully", response));
    }

    @PostMapping(value = "/submissions/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "UC-05: Tải lên file báo cáo PDF/Word lên Cloudinary")
    public ResponseEntity<ApiResponse<SubmissionResponse>> uploadReport(
            @RequestParam Long courseId,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        SubmissionResponse response = submissionService.uploadReport(courseId, file, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Report uploaded successfully", response));
    }

    @GetMapping("/submissions")
    @Operation(summary = "Xem lịch sử nộp bài của bản thân")
    public ResponseEntity<ApiResponse<Page<SubmissionResponse>>> getMySubmissions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(submissionService.getMySubmissions(userDetails.getUsername(), pageable)));
    }

    @GetMapping("/submissions/{id}")
    @Operation(summary = "Xem chi tiết bài nộp (bao gồm điểm và feedback)")
    public ResponseEntity<ApiResponse<SubmissionResponse>> getSubmissionById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(submissionService.getSubmissionById(id, userDetails.getUsername())));
    }

    // ==================== MATERIALS ====================

    @GetMapping("/courses/{courseId}/materials")
    @Operation(summary = "Xem tài liệu bài giảng của khóa học")
    public ResponseEntity<ApiResponse<Page<MaterialResponse>>> getMaterials(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(materialService.getMaterialsByCourse(courseId, pageable)));
    }
}
