package faang.school.projectservice.handler;

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
}
