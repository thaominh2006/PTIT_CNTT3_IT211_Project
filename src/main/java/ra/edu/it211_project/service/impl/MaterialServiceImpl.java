package ra.edu.it211_project.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.edu.it211_project.dto.response.MaterialResponse;
import ra.edu.it211_project.entity.Course;
import ra.edu.it211_project.entity.Material;
import ra.edu.it211_project.exception.ResourceNotFoundException;
import ra.edu.it211_project.repository.CourseRepository;
import ra.edu.it211_project.repository.MaterialRepository;
import ra.edu.it211_project.service.MaterialService;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {
    private final MaterialRepository materialRepository;
    private final CourseRepository courseRepository;

    @Override
    public Page<MaterialResponse> getMaterialsByCourse(Long courseId, Pageable pageable) {
        // Verify course exists
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
        return materialRepository.findByCourseId(courseId, pageable)
                .map(this::mapToResponse);
    }

    private MaterialResponse mapToResponse(Material material) {
        return MaterialResponse.builder()
                .id(material.getId())
                .title(material.getTitle())
                .fileUrl(material.getFileUrl())
                .description(material.getDescription())
                .courseId(material.getCourse().getId())
                .courseName(material.getCourse().getCourseName())
                .uploadedByName(material.getUploadedBy().getFullName())
                .uploadedAt(material.getUploadedAt())
                .build();
    }
}