package ra.edu.it211_project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.it211_project.dto.request.SubmissionRequest;
import ra.edu.it211_project.dto.response.SubmissionResponse;

public interface SubmissionService {
    SubmissionResponse submitAssignment(SubmissionRequest request, String studentUsername);
    SubmissionResponse uploadReport(Long courseId, MultipartFile file, String studentUsername);
    Page<SubmissionResponse> getMySubmissions(String studentUsername, Pageable pageable);
    SubmissionResponse getSubmissionById(Long id, String studentUsername);
}