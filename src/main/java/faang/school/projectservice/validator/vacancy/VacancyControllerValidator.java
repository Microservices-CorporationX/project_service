package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.VacancyDto;
import org.springframework.stereotype.Component;

@Component
public class VacancyControllerValidator {
    public void validateVacancyDto(VacancyDto vacancyDto) {
        if (vacancyDto == null) {
            throw new IllegalArgumentException("Vacancy cannot be null");
        }
    }

    public void validateVacancyId(Long vacancyId) {
        if (vacancyId == null) {
            throw new IllegalArgumentException("Vacancy Id cannot be null");
        }
    }
}
