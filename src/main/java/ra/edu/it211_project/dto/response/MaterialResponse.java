package ra.edu.it211_project.dto.response;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialResponse {
    private Long id;
    private String title;
    private String fileUrl;
    private String description;
    private Long courseId;
    private String courseName;
    private String uploadedByName;
    private LocalDateTime uploadedAt;
}
