package faang.school.projectservice.exceptions.invitation.handler;

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

    @ExceptionHandler(InvitationNotFoundException.class)
    public ResponseEntity<String> handleInvitationNotFoundException(InvitationNotFoundException ex) {
        log.error("[{}] Ошибка: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RejectionReasonMissingException.class)
    public ResponseEntity<String> handleRejectionReasonMissingException(RejectionReasonMissingException ex) {
        log.warn("[{}] Ошибка: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInvitationDataException.class)
    public ResponseEntity<String> handleInvalidInvitationDataException(InvalidInvitationDataException ex) {
        log.warn("[{}] Ошибка: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error("[{}] Непредвиденная ошибка: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("Произошла непредвиденная ошибка: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
