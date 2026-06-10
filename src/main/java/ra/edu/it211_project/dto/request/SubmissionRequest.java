package ra.edu.it211_project.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionRequest {
    @NotNull(message = "Course ID is required")
    private Long courseId;

    private String githubUrl;
}