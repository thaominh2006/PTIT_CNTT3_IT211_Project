package ra.edu.it211_project.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.edu.it211_project.dto.request.CourseRequest;
import ra.edu.it211_project.dto.response.ApiResponse;
import ra.edu.it211_project.dto.response.CourseResponse;

public interface CourseService {
    Page<CourseResponse> getAllCourses(String search, Pageable pageable);
    CourseResponse getCourseById(Long id);
    CourseResponse createCourse(CourseRequest request);
    CourseResponse updateCourse(Long id, CourseRequest request);
    ApiResponse<Void> deleteCourse(Long id);
    ApiResponse<Void> enrollStudent(Long courseId, String username);
    Page<CourseResponse> getEnrolledCourses(String username, Pageable pageable);
}
