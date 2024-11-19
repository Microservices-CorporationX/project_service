package faang.school.projectservice.handler;

import faang.school.projectservice.exception.InvalidStageTransferException;
import faang.school.projectservice.exception.NonExistentDeletionTypeException;
import faang.school.projectservice.exception.ProjectStatusValidationException;
import faang.school.projectservice.exception.Subproject.*;
import jakarta.persistence.EntityNotFoundException;
import faang.school.projectservice.exception.vacancy.VacancyDuplicationException;
import lombok.extern.slf4j.Slf4j;
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
    public ErrorResponse handleVacancyCreation(VacancyDuplicationException exception) {
        log.error("Vacancy Creation Error: {}", exception.getMessage());
        return new ErrorResponse("Vacancy Creation Error: {}", exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleVacancyCreation(IllegalArgumentException exception) {
        log.error("Illegal Argument Error: {}", exception.getMessage());
        return new ErrorResponse("Illegal Argument Error: {}", exception.getMessage());
    }

    @ExceptionHandler(SubprojectBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleSubprojectBadRequestException(SubprojectBadRequestException exception) {
        log.error("Subproject Bad Request Error: {}", exception.getMessage());
        return new ErrorResponse("Subproject Bad Request Error: {}", exception.getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception e) {
        return new ErrorResponse("Internal Server Error", e.getMessage());
    }
}
