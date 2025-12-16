package ogami_api.ogani_website.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard error response structure untuk REST API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private String code;
    private String message;
    private String resource;
    private Object id;
    private LocalDateTime timestamp;

    public ErrorResponse(String code, String message, String resource, Object id) {
        this.code = code;
        this.message = message;
        this.resource = resource;
        this.id = id;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
