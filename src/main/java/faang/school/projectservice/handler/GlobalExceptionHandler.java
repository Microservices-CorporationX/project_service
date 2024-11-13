
import faang.school.projectservice.exception.vacancy.VacancyDuplicationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Data
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(VacancyDuplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleVacancyCreation(VacancyDuplicationException exception){
        log.error("Vacancy Creation Error: {}", exception.getMessage());
        return new ErrorResponse("Vacancy Creation Error: {}", exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleVacancyCreation(EntityNotFoundException exception){
        log.error("Entity Not Found Error: {}", exception.getMessage());
        return new ErrorResponse("Entity Not Found Error: {}", exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleVacancyCreation(IllegalArgumentException exception){
        log.error("Illegal Argument Error: {}", exception.getMessage());
        return new ErrorResponse("Illegal Argument Error: {}", exception.getMessage());
    }
  private void logError(Exception ex, String logLevel) {
        String message = String.format("[%s] Ошибка: %s", ex.getClass().getSimpleName(), ex.getMessage());
        if ("error".equalsIgnoreCase(logLevel)) {
            log.error(message, ex);
        } else if ("warn".equalsIgnoreCase(logLevel)) {
            log.warn(message, ex);
        }
    }

    @ExceptionHandler(InvitationNotFoundException.class)
    public ResponseEntity<String> handleInvitationNotFoundException(InvitationNotFoundException ex) {
        logError(ex, "error");
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RejectionReasonMissingException.class)
    public ResponseEntity<String> handleRejectionReasonMissingException(RejectionReasonMissingException ex) {
        logError(ex, "warn");
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInvitationDataException.class)
    public ResponseEntity<String> handleInvalidInvitationDataException(InvalidInvitationDataException ex) {
        logError(ex, "warn");
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        logError(ex, "error");
        return new ResponseEntity<>("Произошла непредвиденная ошибка: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
}
