package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VacancyValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(VacancyValidator.class);

    public void validateVacancyFields(String name, String description, Project project, int count, Long curatorId, Double salary) {
        LOGGER.info("Validating vacancy fields: name={}, description={}, project={}, count={}, curatorId={}, salary={}",
                name, description, project, count, curatorId, salary);

        if (name == null || name.isBlank()) {
            LOGGER.error("Validation failed: Vacancy name cannot be empty");
            throw new DataValidationException("Vacancy name cannot be empty");
        }
        if (description == null || description.isBlank()) {
            LOGGER.error("Validation failed: Vacancy description cannot be empty");
            throw new DataValidationException("Vacancy description cannot be empty");
        }
        if (project == null) {
            LOGGER.error("Validation failed: Project cannot be empty");
            throw new DataValidationException("Project cannot be empty");
        }
        if (count <= 0) {
            LOGGER.error("Validation failed: Vacancy count must be greater than 0");
            throw new DataValidationException("Vacancy count must be greater than 0");
        }
        if (curatorId == null || curatorId <= 0) {
            LOGGER.error("Validation failed: Curator ID cannot be empty and must be greater than 0");
            throw new DataValidationException("Curator ID cannot be empty and must be greater than 0");
        }
        if (salary == null || salary <= 0) {
            LOGGER.error("Validation failed: Salary must be greater than 0");
            throw new DataValidationException("Salary must be greater than 0");
        }

        LOGGER.info("Validation successful for vacancy fields");
    }

    public void validateVacancyId(long vacancyId) {
        LOGGER.info("Validating vacancy ID: {}", vacancyId);

        if (vacancyId <= 0) {
            LOGGER.error("Validation failed: Invalid vacancy ID");
            throw new DataValidationException("Invalid vacancy ID");
        }

        LOGGER.info("Validation successful for vacancy ID");
    }

    public void validateVacancyIfFound(Optional<Vacancy> vacancy, long vacancyId) {
        if (vacancy.isEmpty()) {
            LOGGER.error("Vacancy with ID {} not found in Database", vacancyId);
            throw new DataValidationException("Vacancy not found");
        }
    }

    public void validateFilters(String name, String position) {
        if (name == null || name.isBlank()) {
            LOGGER.error("Validation failed: Name cannot be empty");
            throw new DataValidationException("Name cannot be empty");
        }
        if (position == null || position.isBlank()) {
            LOGGER.error("Validation failed: Position cannot be empty");
            throw new DataValidationException("Position cannot be empty");
        }
    }

    public void validateCandidate(Candidate candidate) {
        if (candidate == null) {
            LOGGER.error("Validation failed: Candidate cannot be empty");
            throw new DataValidationException("Candidate cannot be empty");
        }
    }
}
