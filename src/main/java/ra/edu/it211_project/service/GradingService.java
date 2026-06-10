package ra.edu.it211_project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.edu.it211_project.dto.request.GradeRequest;
import ra.edu.it211_project.dto.response.SubmissionResponse;

public interface GradingService {
    SubmissionResponse gradeSubmission(GradeRequest request, String lecturerUsername);
    Page<SubmissionResponse> getSubmissionsByCourse(Long courseId, Pageable pageable);
}
