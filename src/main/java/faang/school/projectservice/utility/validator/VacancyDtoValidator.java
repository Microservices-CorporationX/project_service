package faang.school.projectservice.utility.validator;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class VacancyDtoValidator {
    private final Validator validator;

    public boolean validate(VacancyDto vacancyDto) {
        log.debug("validate userDto: {}", vacancyDto);

        Set<ConstraintViolation<VacancyDto>> constraintViolations = validator.validate(vacancyDto);

        if (constraintViolations.isEmpty()) {
            return true;
        }
        StringBuilder message = new StringBuilder();

        constraintViolations.forEach(constraintViolation ->
                message.append(constraintViolation.getMessage()));

        throw new DataValidationException(message.toString());
    }
}
