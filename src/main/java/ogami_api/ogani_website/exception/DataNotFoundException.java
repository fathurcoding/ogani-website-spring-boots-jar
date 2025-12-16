package ogami_api.ogani_website.exception;

import lombok.Getter;

/**
 * Exception thrown ketika data tidak ditemukan di database.
 */
@Getter
public class DataNotFoundException extends RuntimeException {
    
    private final String resourceName;
    private final Object idValue;

    public DataNotFoundException(String resourceName, Object idValue) {
        super(String.format("%s dengan ID %s tidak ditemukan", resourceName, idValue));
        this.resourceName = resourceName;
        this.idValue = idValue;
    }

    public DataNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.idValue = null;
    }
}
