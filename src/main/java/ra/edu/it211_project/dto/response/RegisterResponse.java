package ra.edu.it211_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.edu.it211_project.entity.RoleEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private RoleEnum role;
    private String message;
}
