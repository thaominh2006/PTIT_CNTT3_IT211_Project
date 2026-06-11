package ra.edu.it211_project.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.it211_project.dto.response.ApiResponse;
import ra.edu.it211_project.dto.response.MaterialResponse;
import ra.edu.it211_project.entity.Course;
import ra.edu.it211_project.entity.Material;
import ra.edu.it211_project.entity.User;
import ra.edu.it211_project.exception.InvalidStateException;
import ra.edu.it211_project.exception.ResourceNotFoundException;
import ra.edu.it211_project.repository.CourseRepository;
import ra.edu.it211_project.repository.MaterialRepository;
import ra.edu.it211_project.repository.UserRepository;
import ra.edu.it211_project.service.CloudinaryService;
import ra.edu.it211_project.service.MaterialService;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public MaterialResponse uploadMaterial(Long courseId, String title, MultipartFile file, String lecturerUsername) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        User lecturer = userRepository.findByUsername(lecturerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with username: " + lecturerUsername));

        String fileUrl = cloudinaryService.uploadFile(file, "materials/" + courseId);

        Material material = Material.builder()
                .title(title)
                .fileUrl(fileUrl)
                .course(course)
                .uploadedBy(lecturer)
                .build();

        return mapToResponse(materialRepository.save(material));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaterialResponse> getMaterialsByCourse(Long courseId, Pageable pageable) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
        return materialRepository.findByCourseId(courseId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteMaterial(Long id, String lecturerUsername) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + id));

        if (!material.getUploadedBy().getUsername().equals(lecturerUsername)) {
            throw new InvalidStateException("You are not authorized to delete this material");
        }

        materialRepository.delete(material);
        return ApiResponse.success("Material deleted successfully", null);
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