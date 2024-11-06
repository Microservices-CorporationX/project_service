package faang.school.projectservice.validator;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VacancyValidator {

    public void validateVacancyFields(VacancyDto vacancyDto) {
        log.info("Validating vacancy fields: {}", vacancyDto);

        validateId(vacancyDto.id());
        if (vacancyDto.name() == null || vacancyDto.name().isBlank()) {
            log.error("Validation failed: Vacancy name cannot be empty");
            throw new DataValidationException("Vacancy name cannot be empty");
        }
        if (vacancyDto.description() == null || vacancyDto.description().isBlank()) {
            log.error("Validation failed: Vacancy description cannot be empty");
            throw new DataValidationException("Vacancy description cannot be empty");
        }
        if (vacancyDto.projectId() == null || vacancyDto.projectId() <= 0) {
            log.error("Validation failed: Project id cannot be null and must be greater than 0");
            throw new DataValidationException("Project id cannot be null and must be greater than 0");
        }
        if (vacancyDto.count() <= 0) {
            log.error("Validation failed: Vacancy count must be greater than 0");
            throw new DataValidationException("Vacancy count must be greater than 0");
        }
        if (vacancyDto.createdBy() == null || vacancyDto.createdBy() <= 0) {
            log.error("Validation failed: Curator ID cannot be empty and must be greater than 0");
            throw new DataValidationException("Curator ID cannot be empty and must be greater than 0");
        }

        log.info("Validation successful for vacancy fields");
    }

    public void validateId(Long id) {
        if (id == null || id <= 0) {
            log.error("Validation failed: Vacancy ID cannot be null and must be greater than 0");
            throw new DataValidationException("Vacancy ID cannot be null and must be greater than 0");
        }
    }
}
