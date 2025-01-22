package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.InternshipDto;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class InternshipValidator {

    public void validateForCreation(InternshipDto internshipDto) {
        validateCommonFields(internshipDto);
        ValidationUtils.validateNotNull(internshipDto.getInternIds(), "Intern IDs");
        if (internshipDto.getInternIds().isEmpty()) {
            throw new IllegalArgumentException("Internship must have at least one intern.");
        }
        validateDates(internshipDto);
    }

    public void validateForUpdate(InternshipDto internshipDto) {
        validateCommonFields(internshipDto);
        ValidationUtils.validateNotNull(internshipDto.getId(), "Internship ID");
    }

    public void validatePartialUpdate(InternshipDto internshipDto) {
        if (internshipDto.getStartDate() != null && internshipDto.getEndDate() != null) {
            validateDates(internshipDto);
        }
    }

    private void validateCommonFields(InternshipDto internshipDto) {
        ValidationUtils.validateNotNull(internshipDto.getStartDate(), "Start Date");
        ValidationUtils.validateNotNull(internshipDto.getEndDate(), "End Date");
    }

    private void validateDates(InternshipDto internshipDto) {
        Duration duration = Duration.between(internshipDto.getStartDate(), internshipDto.getEndDate());
        if (duration.toDays() > 90) {
            throw new IllegalArgumentException("Internship cannot last longer than 3 months.");
        }
    }
}