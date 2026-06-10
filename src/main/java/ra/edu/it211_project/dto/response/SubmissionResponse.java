package ra.edu.it211_project.dto.response;
import lombok.*;
import ra.edu.it211_project.entity.SubmissionStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private String reportUrl;
    private String githubUrl;
    private Double score;
    private String feedback;
    private SubmissionStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;
}
