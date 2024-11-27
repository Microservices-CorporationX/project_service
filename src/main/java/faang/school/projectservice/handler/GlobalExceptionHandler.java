package faang.school.projectservice.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.exception.vacancy.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleInvalidInputFormat(NumberFormatException ex) {
        log.error("NumberFormatException occurred: Invalid input format: {}", ex.getMessage());
        return ResponseEntity.badRequest().body("Invalid input: " + ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("ConstraintViolationException occurred: Invalid input: {}", ex.getMessage());
        return ResponseEntity.badRequest().body("Invalid input: " + ex.getMessage());
    }

    @ExceptionHandler({DataValidationException.class,
            InvalidFormatException.class,
            DataIntegrityViolationException.class,
            MethodArgumentNotValidException.class,
            org.springframework.http.converter.HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleExceptions(Exception ex, HttpServletRequest request) {
        String errorMessage = ex.getMessage();
        log.error(ex.getClass() + ": " + errorMessage);
        ErrorResponse errorResponse = getErrorResponse(request, HttpStatus.BAD_REQUEST, errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({EntityNotFoundException.class,
            ProjectNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(Exception ex, HttpServletRequest request) {
        String errorMessage = ex.getMessage();
        log.error(ex.getClass() + ": " + errorMessage);
        ErrorResponse errorResponse = getErrorResponse(request, HttpStatus.NOT_FOUND, errorMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    private static ErrorResponse getErrorResponse(HttpServletRequest request, HttpStatus status, String errorMessage) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(status.value());
        errorResponse.setError(status.getReasonPhrase());
        errorResponse.setMessage(errorMessage);
        errorResponse.setPath(request.getRequestURI());
        return errorResponse;
    }
}
