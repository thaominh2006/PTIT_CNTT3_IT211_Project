package ra.edu.it211_project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ra.edu.it211_project.dto.request.GradeRequest;
import ra.edu.it211_project.dto.response.SubmissionResponse;
import ra.edu.it211_project.entity.*;
import ra.edu.it211_project.exception.InvalidStateException;
import ra.edu.it211_project.exception.ResourceNotFoundException;
import ra.edu.it211_project.repository.CourseRepository;
import ra.edu.it211_project.repository.SubmissionRepository;
import ra.edu.it211_project.repository.UserRepository;
import ra.edu.it211_project.service.impl.GradingServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradingServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private GradingServiceImpl gradingService;

    private User mockLecturer;
    private User mockStudent;
    private Course mockCourse;
    private Submission mockSubmission;

    @BeforeEach
    void setUp() {
        mockLecturer = User.builder()
                .id(1L)
                .username("gv01")
                .fullName("Giang Vien 01")
                .role(RoleEnum.LECTURER)
                .isActive(true)
                .build();

        mockStudent = User.builder()
                .id(2L)
                .username("sv01")
                .fullName("Sinh Vien 01")
                .role(RoleEnum.STUDENT)
                .isActive(true)
                .build();

        mockCourse = Course.builder()
                .id(1L)
                .courseCode("IT211")
                .courseName("Java Web Service")
                .credit(3)
                .lecturer(mockLecturer)
                .build();

        mockSubmission = Submission.builder()
                .id(1L)
                .student(mockStudent)
                .course(mockCourse)
                .githubUrl("https://github.com/test/project")
                .status(SubmissionStatus.SUBMITTED)
                .build();
    }


    @Test
    void gradeSubmission_Success() {
        GradeRequest request = new GradeRequest();
        request.setSubmissionId(1L);
        request.setScore(95.0);
        request.setFeedback("Bai lam tot");

        when(userRepository.findByUsername("gv01")).thenReturn(Optional.of(mockLecturer));
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(mockSubmission));
        when(submissionRepository.save(any(Submission.class))).thenReturn(mockSubmission);

        SubmissionResponse response = gradingService.gradeSubmission(request, "gv01");

        assertNotNull(response);
        verify(submissionRepository, times(1)).save(any(Submission.class));
    }

    @Test
    void gradeSubmission_LecturerNotAssigned_ThrowsException() {
        User otherLecturer = User.builder()
                .id(99L)
                .username("other_lecturer")
                .role(RoleEnum.LECTURER)
                .build();

        GradeRequest request = new GradeRequest();
        request.setSubmissionId(1L);
        request.setScore(90.0);
        request.setFeedback("test");

        when(userRepository.findByUsername("other_lecturer")).thenReturn(Optional.of(otherLecturer));
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(mockSubmission));

        assertThrows(InvalidStateException.class,
                () -> gradingService.gradeSubmission(request, "other_lecturer"));
    }

    @Test
    void gradeSubmission_SubmissionNotFound_ThrowsException() {
        GradeRequest request = new GradeRequest();
        request.setSubmissionId(99L);
        request.setScore(90.0);

        when(userRepository.findByUsername("gv01")).thenReturn(Optional.of(mockLecturer));
        when(submissionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> gradingService.gradeSubmission(request, "gv01"));
    }

    @Test
    void gradeSubmission_StatusChangedToGraded() {
        GradeRequest request = new GradeRequest();
        request.setSubmissionId(1L);
        request.setScore(85.0);
        request.setFeedback("Good");

        when(userRepository.findByUsername("gv01")).thenReturn(Optional.of(mockLecturer));
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(mockSubmission));
        when(submissionRepository.save(any(Submission.class))).thenAnswer(inv -> {
            Submission s = inv.getArgument(0);
            assertEquals(SubmissionStatus.GRADED, s.getStatus());
            assertEquals(85.0, s.getScore());
            return s;
        });

        gradingService.gradeSubmission(request, "gv01");
        verify(submissionRepository).save(any(Submission.class));
    }
}