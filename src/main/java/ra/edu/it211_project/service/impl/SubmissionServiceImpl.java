package ra.edu.it211_project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.it211_project.dto.request.SubmissionRequest;
import ra.edu.it211_project.dto.response.SubmissionResponse;
import ra.edu.it211_project.entity.Course;
import ra.edu.it211_project.entity.Submission;
import ra.edu.it211_project.entity.SubmissionStatus;
import ra.edu.it211_project.entity.User;
import ra.edu.it211_project.exception.InvalidStateException;
import ra.edu.it211_project.exception.ResourceNotFoundException;
import ra.edu.it211_project.repository.CourseRepository;
import ra.edu.it211_project.repository.SubmissionRepository;
import ra.edu.it211_project.repository.UserRepository;
import ra.edu.it211_project.service.CloudinaryService;
import ra.edu.it211_project.service.SubmissionService;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public SubmissionResponse submitAssignment(SubmissionRequest request, String studentUsername) {
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // Check if already submitted
        if (submissionRepository.findByStudentIdAndCourseId(student.getId(), course.getId()).isPresent()) {
            throw new InvalidStateException("You have already submitted for this course");
        }

        Submission submission = Submission.builder()
                .student(student)
                .course(course)
                .githubUrl(request.getGithubUrl())
                .status(SubmissionStatus.SUBMITTED)
                .build();

        return mapToResponse(submissionRepository.save(submission));
    }

    @Override
    @Transactional
    public SubmissionResponse uploadReport(Long courseId, MultipartFile file, String studentUsername) {
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Submission submission = submissionRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                .orElseThrow(() -> new InvalidStateException("Please submit your assignment first"));

        String fileUrl = cloudinaryService.uploadFile(file, "reports/" + courseId);
        submission.setReportUrl(fileUrl);
        submission.setStatus(SubmissionStatus.SUBMITTED);

        return mapToResponse(submissionRepository.save(submission));
    }

    @Override
    public Page<SubmissionResponse> getMySubmissions(String studentUsername, Pageable pageable) {
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return submissionRepository.findByStudentId(student.getId(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    public SubmissionResponse getSubmissionById(Long id, String studentUsername) {
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        if (!submission.getStudent().getId().equals(student.getId())) {
            throw new InvalidStateException("You can only view your own submissions");
        }

        return mapToResponse(submission);
    }

    private SubmissionResponse mapToResponse(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .studentId(submission.getStudent().getId())
                .studentName(submission.getStudent().getFullName())
                .courseId(submission.getCourse().getId())
                .courseName(submission.getCourse().getCourseName())
                .reportUrl(submission.getReportUrl())
                .githubUrl(submission.getGithubUrl())
                .score(submission.getScore())
                .feedback(submission.getFeedback())
                .status(submission.getStatus())
                .submittedAt(submission.getSubmittedAt())
                .updatedAt(submission.getUpdatedAt())
                .build();
    }
}