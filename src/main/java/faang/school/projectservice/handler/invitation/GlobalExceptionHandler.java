package faang.school.projectservice.handler.invitation;

import faang.school.projectservice.exceptions.invitation.InvitationNotFoundException;
import faang.school.projectservice.exceptions.invitation.RejectionReasonMissingException;
import faang.school.projectservice.exceptions.invitation.InvalidInvitationDataException;
import faang.school.projectservice.exceptions.invitation.StageNotFoundException;
import faang.school.projectservice.exceptions.invitation.TeamMemberNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;


public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvitationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInvitationNotFound(InvitationNotFoundException ex) {
        log.error("Приглашение не найдено: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "INVITATION_NOT_FOUND");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RejectionReasonMissingException.class)
    public ResponseEntity<ErrorResponse> handleRejectionReasonMissing(RejectionReasonMissingException ex) {
        log.warn("Причина отклонения отсутствует: ", ex);
        ErrorResponse errorResponse = new ErrorResponse("Причина отклонения обязательна", "REJECTION_REASON_MISSING");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInvitationDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInvitationData(InvalidInvitationDataException ex) {
        log.error("Неверные данные приглашения: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "INVALID_INVITATION_DATA");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStageNotFound(StageNotFoundException ex) {
        log.error("Этап не найден: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "STAGE_NOT_FOUND");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TeamMemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTeamMemberNotFound(TeamMemberNotFoundException ex) {
        log.error("Член команды не найден: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "TEAM_MEMBER_NOT_FOUND");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Произошло необработанное исключение: ", ex);
        ErrorResponse errorResponse = new ErrorResponse("Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже.", "INTERNAL_SERVER_ERROR");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
