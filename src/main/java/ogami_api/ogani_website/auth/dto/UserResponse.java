package ogami_api.ogani_website.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ogami_api.ogani_website.user.model.UserRole;

import java.time.LocalDate;

/**
 * DTO untuk user response dalam auth endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Integer userId;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private LocalDate birthDate;
    private Integer age;
    private String address;
    private UserRole role;
}
