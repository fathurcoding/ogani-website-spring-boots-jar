package ogami_api.ogani_website.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global Exception Handler untuk centralized error handling.
 * Menangani semua exception yang terjadi di controller dan service layer.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDataNotFound(DataNotFoundException ex) {
        return new ErrorResponse(
                "DATA_NOT_FOUND",
                ex.getMessage(),
                ex.getResourceName() != null ? ex.getResourceName() : null,
                ex.getIdValue() != null ? ex.getIdValue() : null
        );
    }

    @ExceptionHandler(DataAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataAlreadyExists(DataAlreadyExistsException ex) {
        return new ErrorResponse(
                "DATA_ALREADY_EXISTS",
                ex.getMessage(),
                ex.getResourceName() != null ? ex.getResourceName() : null,
                ex.getIdValue() != null ? ex.getIdValue() : null
        );
    }

    @ExceptionHandler(InsufficientStockException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInsufficientStock(InsufficientStockException ex) {
        return new ErrorResponse(
                "INSUFFICIENT_STOCK",
                ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        return new ErrorResponse(
                "BAD_REQUEST",
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .orElse("Data tidak valid");
        
        return new ErrorResponse(
                "VALIDATION_ERROR",
                message
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception ex) {
        return new ErrorResponse(
                "INTERNAL_ERROR",
                ex.getMessage()
        );
    }
}
