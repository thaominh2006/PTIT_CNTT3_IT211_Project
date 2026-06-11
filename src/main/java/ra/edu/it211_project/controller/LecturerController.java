package ra.edu.it211_project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.it211_project.dto.request.GradeRequest;
import ra.edu.it211_project.dto.response.ApiResponse;
import ra.edu.it211_project.dto.response.MaterialResponse;
import ra.edu.it211_project.dto.response.SubmissionResponse;
import ra.edu.it211_project.service.GradingService;
import ra.edu.it211_project.service.MaterialService;

@RestController
@RequestMapping("/api/v1/lecturer")
@RequiredArgsConstructor
@Tag(name = "Lecturer", description = "FR-08: Chấm điểm & FR-09: Upload tài liệu bài giảng")
public class LecturerController {

    private final GradingService gradingService;
    private final MaterialService materialService;

    // ==================== FR-08: GRADING ====================

    @PostMapping("/grades")
    @Operation(summary = "FR-08: Chấm điểm đồ án & ghi feedback")
    public ResponseEntity<ApiResponse<SubmissionResponse>> gradeSubmission(
            @Valid @RequestBody GradeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        SubmissionResponse response = gradingService.gradeSubmission(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Grade submitted successfully", response));
    }

    @GetMapping("/submissions")
    @Operation(summary = "FR-08: Xem danh sách bài nộp theo khóa học")
    public ResponseEntity<ApiResponse<Page<SubmissionResponse>>> getSubmissionsByCourse(
            @RequestParam Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(gradingService.getSubmissionsByCourse(courseId, pageable)));
    }

    @GetMapping("/submissions/{id}")
    @Operation(summary = "FR-08: Xem chi tiết bài nộp")
    public ResponseEntity<ApiResponse<SubmissionResponse>> getSubmissionById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(gradingService.getSubmissionById(id)));
    }

    // ==================== FR-09: MATERIALS ====================

    @PostMapping(value = "/materials", consumes = "multipart/form-data")
    @Operation(summary = "FR-09: Tải lên tài liệu bài giảng lên Cloudinary")
    public ResponseEntity<ApiResponse<MaterialResponse>> uploadMaterial(
            @RequestParam Long courseId,
            @RequestParam String title,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        MaterialResponse response = materialService.uploadMaterial(courseId, title, file, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Material uploaded successfully", response));
    }

    @GetMapping("/materials")
    @Operation(summary = "FR-09: Xem danh sách tài liệu theo khóa học")
    public ResponseEntity<ApiResponse<Page<MaterialResponse>>> getMaterialsByCourse(
            @RequestParam Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(materialService.getMaterialsByCourse(courseId, pageable)));
    }

    @DeleteMapping("/materials/{id}")
    @Operation(summary = "FR-09: Xóa tài liệu bài giảng")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(materialService.deleteMaterial(id, userDetails.getUsername()));
    }
}