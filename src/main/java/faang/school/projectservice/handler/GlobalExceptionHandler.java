package faang.school.projectservice.handler;

import faang.school.projectservice.exceptions.invitation.InvitationNotFoundException;
import faang.school.projectservice.exceptions.invitation.RejectionReasonMissingException;
import faang.school.projectservice.exceptions.invitation.InvalidInvitationDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

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
}
