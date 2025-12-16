package ogami_api.ogani_website.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ogami_api.ogani_website.user.model.UserRole;

/**
 * DTO untuk login response.
 * Contains JWT token dan user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    
    @Builder.Default
    private String type = "Bearer";
    
    private Integer userId;
    
    private String username;
    
    private String email;
    
    private UserRole role;
    
    private Long expiresIn;  // Seconds until token expires
}
