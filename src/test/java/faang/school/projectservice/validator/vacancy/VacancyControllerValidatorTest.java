package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.VacancyDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VacancyControllerValidatorTest {

    private VacancyControllerValidator vacancyControllerValidator = new VacancyControllerValidator();

    @Test
    void validateVacancyDtoSuccess() {
        assertDoesNotThrow(() -> vacancyControllerValidator.validateVacancyDto(new VacancyDto()));
    }

    @Test
    void validateVacancyDto_Null() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vacancyControllerValidator.validateVacancyDto(null));
        assertEquals("Vacancy cannot be null", exception.getMessage());
    }

    @Test
    void validateVacancyId() {
        assertDoesNotThrow(() -> vacancyControllerValidator.validateVacancyId(1L));
    }

    @Test
    void validateVacancyId_Null() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vacancyControllerValidator.validateVacancyId(null));
        assertEquals("Vacancy Id cannot be null", exception.getMessage());
    }
}