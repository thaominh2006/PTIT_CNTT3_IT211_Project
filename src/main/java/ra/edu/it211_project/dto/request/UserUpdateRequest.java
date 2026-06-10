package ra.edu.it211_project.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import ra.edu.it211_project.entity.RoleEnum;

@Data
public class UserUpdateRequest {
    @Email(message = "Invalid email format")
    private String email;

    private String fullName;

    private RoleEnum role;

    private Boolean isActive;
}
