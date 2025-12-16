package ogami_api.ogani_website.exception;

/**
 * Exception untuk duplicate data (username, email, phone sudah ada).
 */
public class DuplicateDataException extends RuntimeException {
    
    public DuplicateDataException(String message) {
        super(message);
    }
    
    public DuplicateDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
