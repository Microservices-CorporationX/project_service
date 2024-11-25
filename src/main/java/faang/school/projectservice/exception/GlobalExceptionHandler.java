package faang.school.projectservice.exception;

import feign.FeignException;
import feign.RetryableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EntityNotFoundException.class, FeignException.NotFound.class})
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(RuntimeException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(MessageError.ENTITY_NOT_FOUND_EXCEPTION.name())
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RetryableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleServiceUnavailableException(RetryableException exception) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error("SERVICE_UNAVAILABLE")
                .message(exception.getMessage())
                .build();
    }
}
