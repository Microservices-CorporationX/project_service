package faang.school.projectservice.controller;

import faang.school.projectservice.exceptions.ErrorResponse;
import faang.school.projectservice.exceptions.InvalidUserException;
import faang.school.projectservice.exceptions.MomentExistingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e) {
        log.error("Runtime Exception: {}", e.getMessage());

        return new ErrorResponse("Internal Server error", e.getMessage());
    }

    @ExceptionHandler(InvalidUserException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleInvalidUserException(InvalidUserException e) {
        log.error("InvalidUserException: {}", e.getMessage());

        return new ErrorResponse("Not Found error", e.getMessage());
    }

    @ExceptionHandler(MomentExistingException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMomentExistingException(MomentExistingException e) {
        log.error("MomentExistingException: {}", e.getMessage());

        return new ErrorResponse("Not Found error", e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundException: {}", e.getMessage());

        return new ErrorResponse("Not Found error", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage());

        return e.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), ""))
                );
    }
}
