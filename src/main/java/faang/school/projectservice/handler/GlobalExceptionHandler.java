package faang.school.projectservice.handler;

import faang.school.projectservice.exception.ExternalServiceException;
import faang.school.projectservice.exception.InvalidStageTransferException;
import faang.school.projectservice.exception.NonExistentDeletionTypeException;
import faang.school.projectservice.exception.ProjectStatusValidationException;
import faang.school.projectservice.exception.TeamMemberValidationException;
import faang.school.projectservice.exception.UnauthorizedAccessException;
import faang.school.projectservice.exception.Subproject.*;
import faang.school.projectservice.exception.*;
import jakarta.persistence.EntityNotFoundException;
import faang.school.projectservice.exception.vacancy.VacancyDuplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFound(EntityNotFoundException e) {
        log.error("Entity Not Found", e);
        return new ErrorResponse("Entity Not Found", e.getMessage());
    }

    @ExceptionHandler(ProjectStatusValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleProjectStatusValidation(ProjectStatusValidationException e) {
        log.error("Project status validation failure", e);
        return new ErrorResponse("Project status validation failure", e.getMessage());
    }

    @ExceptionHandler(InvalidStageTransferException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidStageTransfer(InvalidStageTransferException e) {
        log.error("Invalid target stage ID", e);
        return new ErrorResponse("Invalid target stage ID", e.getMessage());
    }

    @ExceptionHandler(NonExistentDeletionTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNonExistentDeletionType(NonExistentDeletionTypeException e) {
        log.error("Unknown deletion type", e);
        return new ErrorResponse("Invalid target stage ID", e.getMessage());
    }

    @ExceptionHandler(VacancyDuplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleVacancyCreation(VacancyDuplicationException e){
        log.error("Vacancy Creation Error", e);
        return new ErrorResponse("Vacancy Creation Error: {}", e.getMessage());
    public ErrorResponse handleVacancyCreation(VacancyDuplicationException exception) {
        log.error("Vacancy Creation Error: {}", exception.getMessage());
        return new ErrorResponse("Vacancy Creation Error: {}", exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleVacancyCreation(IllegalArgumentException e){
        log.error("Illegal Argument Error", e);
        return new ErrorResponse("Illegal Argument Error: {}", e.getMessage());
    }

    @ExceptionHandler(StorageSizeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStorageSizeLimit(StorageSizeException e){
        log.error("Storage size is exceeded", e);
        return new ErrorResponse("Vacancy Creation Error: {}", e.getMessage());
    }

    @ExceptionHandler(PermissionDeniedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTeamMemberPermission(PermissionDeniedException e){
        log.error("Team member userId not found", e);
        return new ErrorResponse("Team member userId not found", e.getMessage());
    public ErrorResponse handleVacancyCreation(IllegalArgumentException exception) {
        log.error("Illegal Argument Error: {}", exception.getMessage());
        return new ErrorResponse("Illegal Argument Error: {}", exception.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDataAccessException(DataAccessException e) {
        log.error("Database error occurred: ", e);
        return new ErrorResponse("Database error", "An error occurred while accessing the database.");
    }

    @ExceptionHandler(ExternalServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleExternalServiceException(ExternalServiceException e) {
        log.error("Interaction failure", e);
        return new ErrorResponse("Interaction failure", e.getMessage());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorizedAccessException(UnauthorizedAccessException e) {
        log.error("Unauthorized access", e);
        return new ErrorResponse("Unauthorized access", e.getMessage());
    }

    @ExceptionHandler(TeamMemberValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTeamMemberValidationException(TeamMemberValidationException e) {
        log.error("Team member validation failure", e);
        return new ErrorResponse("Team member validation failure", e.getMessage());
    }

    @ExceptionHandler(SubprojectBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleSubprojectBadRequestException(SubprojectBadRequestException exception) {
        log.error("Subproject Bad Request Error: {}", exception.getMessage());
        return new ErrorResponse("Subproject Bad Request Error: {}", exception.getMessage());
    }
}
