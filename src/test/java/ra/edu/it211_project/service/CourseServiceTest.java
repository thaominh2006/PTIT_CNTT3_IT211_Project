package ra.edu.it211_project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ra.edu.it211_project.dto.request.CourseRequest;
import ra.edu.it211_project.dto.response.ApiResponse;
import ra.edu.it211_project.dto.response.CourseResponse;
import ra.edu.it211_project.entity.Course;
import ra.edu.it211_project.entity.RoleEnum;
import ra.edu.it211_project.entity.User;
import ra.edu.it211_project.exception.DuplicateResourceException;
import ra.edu.it211_project.exception.ResourceNotFoundException;
import ra.edu.it211_project.repository.CourseRepository;
import ra.edu.it211_project.repository.UserRepository;
import ra.edu.it211_project.service.impl.CourseServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course mockCourse;
    private User mockLecturer;

    @BeforeEach
    void setUp() {
        mockLecturer = User.builder()
                .id(1L)
                .username("lecturer01")
                .fullName("Nguyen Van A")
                .role(RoleEnum.LECTURER)
                .isActive(true)
                .build();

        mockCourse = Course.builder()
                .id(1L)
                .courseCode("IT211")
                .courseName("Java Web Service")
                .credit(3)
                .description("RESTful API")
                .lecturer(mockLecturer)
                .build();
    }


    @Test
    void getAllCourses_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Course> coursePage = new PageImpl<>(List.of(mockCourse));

        when(courseRepository.findByCourseNameContainingIgnoreCase("", pageable))
                .thenReturn(coursePage);

        Page<CourseResponse> result = courseService.getAllCourses(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("IT211", result.getContent().get(0).getCourseCode());
    }


    @Test
    void getCourseById_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(mockCourse));

        CourseResponse response = courseService.getCourseById(1L);

        assertNotNull(response);
        assertEquals("IT211", response.getCourseCode());
        assertEquals("Java Web Service", response.getCourseName());
    }

    @Test
    void getCourseById_NotFound_ThrowsException() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseById(99L));
    }


    @Test
    void createCourse_Success() {
        CourseRequest request = new CourseRequest();
        request.setCourseCode("IT212");
        request.setCourseName("Co so du lieu");
        request.setCredit(3);
        request.setLecturerId(1L);

        when(courseRepository.existsByCourseCode("IT212")).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockLecturer));
        when(courseRepository.save(any(Course.class))).thenReturn(mockCourse);

        CourseResponse response = courseService.createCourse(request);

        assertNotNull(response);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void createCourse_DuplicateCode_ThrowsException() {
        CourseRequest request = new CourseRequest();
        request.setCourseCode("IT211");
        request.setCourseName("Duplicate Course");
        request.setCredit(3);

        when(courseRepository.existsByCourseCode("IT211")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> courseService.createCourse(request));
        verify(courseRepository, never()).save(any(Course.class));
    }


    @Test
    void deleteCourse_Success() {
        when(courseRepository.existsById(1L)).thenReturn(true);
        doNothing().when(courseRepository).deleteById(1L);

        ApiResponse<Void> response = courseService.deleteCourse(1L);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCourse_NotFound_ThrowsException() {
        when(courseRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> courseService.deleteCourse(99L));
        verify(courseRepository, never()).deleteById(any());
    }


    @Test
    void enrollStudent_DuplicateEnroll_ThrowsException() {
        User student = User.builder()
                .id(2L)
                .username("student01")
                .role(RoleEnum.STUDENT)
                .build();

        mockCourse.getStudents().add(student);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(mockCourse));
        when(userRepository.findByUsername("student01")).thenReturn(Optional.of(student));

        assertThrows(DuplicateResourceException.class,
                () -> courseService.enrollStudent(1L, "student01"));
    }
}