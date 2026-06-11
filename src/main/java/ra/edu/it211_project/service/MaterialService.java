package ra.edu.it211_project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.it211_project.dto.response.ApiResponse;
import ra.edu.it211_project.dto.response.MaterialResponse;

public interface MaterialService {
    MaterialResponse uploadMaterial(Long courseId, String title, MultipartFile file, String lecturerUsername);
    Page<MaterialResponse> getMaterialsByCourse(Long courseId, Pageable pageable);
    ApiResponse<Void> deleteMaterial(Long id, String lecturerUsername);
}