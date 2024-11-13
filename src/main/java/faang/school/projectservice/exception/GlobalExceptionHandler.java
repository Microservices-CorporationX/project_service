package faang.school.projectservice.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String ENTITY_NOT_FOUND = "EntityNotFoundException: {}";
    private static final String CONSTRAINT_VIOLATION = "ConstraintViolationException: {}";
    private static final String DATA_VALIDATION = "DataValidationException: {}";
    private static final String METHOD_ARGUMENT_NOT_VALID = "MethodArgumentNotValidException: {}";
    private static final String UNEXPECTED_ERROR = "An unexpected error has occurred: {}";

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error(CONSTRAINT_VIOLATION, ex.getMessage());
        ErrorResponse errorResponse = getErrorResponse(ex, HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(MethodArgumentNotValidException ex) {
        log.error(METHOD_ARGUMENT_NOT_VALID, ex.getMessage());
        ErrorResponse errorResponse = getErrorResponse(ex, HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorResponse> handleDataValidationException(DataValidationException ex) {
        log.error(DATA_VALIDATION, ex.getMessage());
        ErrorResponse errorResponse = getErrorResponse(ex, HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({EntityNotFoundException.class, JpaObjectRetrievalFailureException.class})
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error(ENTITY_NOT_FOUND, ex.getMessage());
        ErrorResponse errorResponse = getErrorResponse(ex, HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {
        log.error(UNEXPECTED_ERROR, ex.getMessage());
        ErrorResponse errorResponse = getErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private ErrorResponse getErrorResponse(Exception ex, int returnCode) {
        return ErrorResponse.builder()
                .status(returnCode)
                .message(ex.getMessage())
                .localDateTime(LocalDateTime.now())
                .build();
    }
}
