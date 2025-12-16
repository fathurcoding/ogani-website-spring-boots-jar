package ui.ft.ccit.faculty.transaksi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public ErrorResponse handleDataAlreadyExists(DataAlreadyExistsException ex) {
        return new ErrorResponse(
                "DATA_ALREADY_EXISTS",
                ex.getMessage(),
                ex.getResourceName(),
                ex.getIdValue());
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorResponse handleDataNotFound(DataNotFoundException ex) {
        return new ErrorResponse(
                "DATA_NOT_FOUND",
                ex.getMessage(),
                ex.getResourceName(),
                ex.getIdValue());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        return new ErrorResponse(
                "BAD_REQUEST",
                ex.getMessage(),
                null,
                null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .orElse("Data tidak valid");
        return new ErrorResponse(
                "VALIDATION_ERROR",
                msg,
                null,
                null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // fallback
    public ErrorResponse handleGeneric(Exception ex) {
        return new ErrorResponse(
                "INTERNAL_ERROR",
                ex.getMessage(),
                null,
                null);
    }

    public static class ErrorResponse {
        private final String code;
        private final String message;
        private final String resource;
        private final String id;
        private final LocalDateTime timestamp;

        public ErrorResponse(String code, String message, String resource, String id) {
            this.code = code;
            this.message = message;
            this.resource = resource;
            this.id = id;
            this.timestamp = LocalDateTime.now();
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getResource() {
            return resource;
        }

        public String getId() {
            return id;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
