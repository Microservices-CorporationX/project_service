package faang.school.projectservice.handler;

import faang.school.projectservice.exception.vacancy.VacancyDuplicationException;
import faang.school.projectservice.exceptions.invitation.InvalidInvitationDataException;
import faang.school.projectservice.exceptions.invitation.InvitationNotFoundException;
import faang.school.projectservice.exceptions.invitation.StageNotFoundException;
import faang.school.projectservice.exceptions.invitation.TeamMemberNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Data
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(VacancyDuplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleVacancyCreation(VacancyDuplicationException exception) {
        log.error("Vacancy Creation Error: {}", exception.getMessage());
        return new ErrorResponse("Vacancy Creation Error: {}", exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleVacancyCreation(EntityNotFoundException exception) {
        log.error("Entity Not Found Error: {}", exception.getMessage());
        return new ErrorResponse("Entity Not Found Error: {}", exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleVacancyCreation(IllegalArgumentException exception) {
        log.error("Illegal Argument Error: {}", exception.getMessage());
        return new ErrorResponse("Illegal Argument Error: {}", exception.getMessage());
    }


    @ExceptionHandler(InvitationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInvitationNotFound(InvitationNotFoundException ex) {
        log.error("Приглашение не найдено: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "INVITATION_NOT_FOUND");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
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
