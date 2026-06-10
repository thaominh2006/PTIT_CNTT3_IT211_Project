package ra.edu.it211_project.dto.response;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseResponse {
    private Long id;
    private String courseCode;
    private String courseName;
    private Integer credit;
    private String description;
    private String lecturerName;
    private Long lecturerId;
}
