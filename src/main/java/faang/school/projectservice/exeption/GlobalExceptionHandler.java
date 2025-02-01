package faang.school.projectservice.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
        return buildValidationExMap(ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleEnumParseException(HttpMessageNotReadableException ex) {
        log.error("Http message not readable exception", ex);
        return "Cannot parse JSON data. Please check it again.";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Entity not found exception", ex);
        return ex.getMessage();
    }

    @ExceptionHandler(NotUniqueProjectException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleNotUniqueProjectException(NotUniqueProjectException ex) {
        log.error("Not unique project exception", ex);
        return ex.getMessage();
    }

    @ExceptionHandler(ProjectNotClosableException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public String handleProjectNotClosableException(ProjectNotClosableException ex) {
        log.error("Project not closable exception", ex);
        return ex.getMessage();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(RuntimeException ex) {
        log.error("Runtime Exception", ex);
        return "An unexpected error occurred.";
    }

    private Map<String, String> buildValidationExMap(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getAllErrors()
                .forEach(err -> errors.put(
                        ((FieldError) err).getField(),
                        err.getDefaultMessage()));
        log.error("Method argument not valid exception", ex);
        return errors;
    }
}
