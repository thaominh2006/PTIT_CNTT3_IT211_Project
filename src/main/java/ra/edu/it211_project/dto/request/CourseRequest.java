package ra.edu.it211_project.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseRequest {
    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotBlank(message = "Course name is required")
    private String courseName;

    @NotNull(message = "Credit is required")
    @Min(value = 1, message = "Credit must be at least 1")
    private Integer credit;

    private String description;

    private Long lecturerId;
}
