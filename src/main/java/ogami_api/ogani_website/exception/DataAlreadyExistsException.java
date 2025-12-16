package ogami_api.ogani_website.exception;

import lombok.Getter;

/**
 * Exception thrown ketika data sudah ada (duplicate).
 */
@Getter
public class DataAlreadyExistsException extends RuntimeException {
    
    private final String resourceName;
    private final Object idValue;

    public DataAlreadyExistsException(String resourceName, Object idValue) {
        super(String.format("%s dengan ID %s sudah ada", resourceName, idValue));
        this.resourceName = resourceName;
        this.idValue = idValue;
    }

    public DataAlreadyExistsException(String message) {
        super(message);
        this.resourceName = null;
        this.idValue = null;
    }
}
