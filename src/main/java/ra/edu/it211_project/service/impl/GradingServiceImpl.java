package ra.edu.it211_project.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.edu.it211_project.dto.request.GradeRequest;
import ra.edu.it211_project.dto.response.SubmissionResponse;
import ra.edu.it211_project.entity.Submission;
import ra.edu.it211_project.entity.SubmissionStatus;
import ra.edu.it211_project.entity.User;
import ra.edu.it211_project.exception.InvalidStateException;
import ra.edu.it211_project.exception.ResourceNotFoundException;
import ra.edu.it211_project.repository.CourseRepository;
import ra.edu.it211_project.repository.SubmissionRepository;
import ra.edu.it211_project.repository.UserRepository;
import ra.edu.it211_project.service.GradingService;

@Service
@RequiredArgsConstructor
public class GradingServiceImpl implements GradingService {
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public SubmissionResponse gradeSubmission(GradeRequest request, String lecturerUsername) {
        User lecturer = userRepository.findByUsername(lecturerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found"));

        Submission submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        // Verify lecturer teaches this course
        if (submission.getCourse().getLecturer() == null ||
                !submission.getCourse().getLecturer().getId().equals(lecturer.getId())) {
            throw new InvalidStateException("You are not assigned to grade this submission");
        }

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setLecturer(lecturer);
        submission.setStatus(SubmissionStatus.GRADED);

        return mapToResponse(submissionRepository.save(submission));
    }

    @Override
    public Page<SubmissionResponse> getSubmissionsByCourse(Long courseId, Pageable pageable) {
        return submissionRepository.findByCourseId(courseId, pageable)
                .map(this::mapToResponse);
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