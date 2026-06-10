package ra.edu.it211_project.dto.response;
import lombok.*;
import ra.edu.it211_project.entity.RoleEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private RoleEnum role;
    private Boolean isActive;
}
