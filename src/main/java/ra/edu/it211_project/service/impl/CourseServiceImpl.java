package ra.edu.it211_project.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.edu.it211_project.dto.request.CourseRequest;
import ra.edu.it211_project.dto.response.ApiResponse;
import ra.edu.it211_project.dto.response.CourseResponse;
import ra.edu.it211_project.entity.Course;
import ra.edu.it211_project.entity.User;
import ra.edu.it211_project.exception.DuplicateResourceException;
import ra.edu.it211_project.exception.ResourceNotFoundException;
import ra.edu.it211_project.repository.CourseRepository;
import ra.edu.it211_project.repository.UserRepository;
import ra.edu.it211_project.service.CourseService;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getAllCourses(String search, Pageable pageable) {
        String keyword = (search != null) ? search : "";
        return courseRepository.findByCourseNameContainingIgnoreCase(keyword, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return mapToResponse(course);
    }

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new DuplicateResourceException("Course code already exists: " + request.getCourseCode());
        }

        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .courseName(request.getCourseName())
                .credit(request.getCredit())
                .description(request.getDescription())
                .build();

        if (request.getLecturerId() != null) {
            User lecturer = userRepository.findById(request.getLecturerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found"));
            course.setLecturer(lecturer);
        }

        return mapToResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setCredit(request.getCredit());
        if (request.getDescription() != null) course.setDescription(request.getDescription());

        if (request.getLecturerId() != null) {
            User lecturer = userRepository.findById(request.getLecturerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found"));
            course.setLecturer(lecturer);
        }

        return mapToResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
        return ApiResponse.success("Course deleted successfully", null);
    }

    @Override
    @Transactional
    public ApiResponse<Void> enrollStudent(Long courseId, String username) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean alreadyEnrolled = course.getStudents().stream()
                .anyMatch(s -> s.getId().equals(student.getId()));

        if (alreadyEnrolled) {
            throw new DuplicateResourceException("Student is already enrolled in this course");
        }

        course.getStudents().add(student);
        courseRepository.save(course);

        return ApiResponse.success("Enrolled in course successfully", null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getEnrolledCourses(String username, Pageable pageable) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return courseRepository.findByStudentId(student.getId(), pageable)
                .map(this::mapToResponse);
    }

    private CourseResponse mapToResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .credit(course.getCredit())
                .description(course.getDescription())
                .lecturerId(course.getLecturer() != null ? course.getLecturer().getId() : null)
                .lecturerName(course.getLecturer() != null ? course.getLecturer().getFullName() : null)
                .build();
    }
}