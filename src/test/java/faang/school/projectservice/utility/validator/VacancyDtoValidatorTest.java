package faang.school.projectservice.utility.validator;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VacancyDtoValidatorTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    VacancyDtoValidator vacancyDtoValidator = new VacancyDtoValidator(validator);

    @Test
    void validate() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("test")
                .description("test")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .createdBy(1L)
                .workSchedule(WorkSchedule.SHIFT_WORK)
                .count(2)
                .build();
        boolean actual = vacancyDtoValidator.validate(vacancyDto);
        Assertions.assertTrue(actual);
    }

    @Test
    void validateNameIsEmpty() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("")
                .description("test")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .createdBy(1L)
                .workSchedule(WorkSchedule.SHIFT_WORK)
                .count(2)
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyDtoValidator.validate(vacancyDto),
                "name must be not empty");
    }

    @Test
    void validateDescriptionIsEmpty() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("test")
                .description("")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .createdBy(1L)
                .workSchedule(WorkSchedule.SHIFT_WORK)
                .count(2)
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyDtoValidator.validate(vacancyDto),
                "description must be not empty");
    }

    @Test
    void validatePositionIsNull() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("test")
                .description("test")
                .projectId(1L)
                .createdBy(1L)
                .workSchedule(WorkSchedule.SHIFT_WORK)
                .count(2)
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyDtoValidator.validate(vacancyDto),
                "position must be not null");
    }

    @Test
    void validateProjectIdIsNull() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("test")
                .description("test")
                .position(TeamRole.ANALYST)
                .createdBy(1L)
                .workSchedule(WorkSchedule.SHIFT_WORK)
                .count(2)
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyDtoValidator.validate(vacancyDto),
                "projectId must be not null");
    }

    @Test
    void validateProjectIdIsLessZero() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("test")
                .description("test")
                .position(TeamRole.ANALYST)
                .projectId(-1L)
                .createdBy(1L)
                .workSchedule(WorkSchedule.SHIFT_WORK)
                .count(2)
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyDtoValidator.validate(vacancyDto),
                "projectId must be minimum 0");
    }

    @Test
    void validateCreateByIsNull() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("test")
                .description("test")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .workSchedule(WorkSchedule.SHIFT_WORK)
                .count(2)
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyDtoValidator.validate(vacancyDto),
                "createBy must be not null");
    }

    @Test
    void validateCreateByIsLessZero() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("test")
                .description("test")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .createdBy(-1L)
                .workSchedule(WorkSchedule.SHIFT_WORK)
                .count(2)
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyDtoValidator.validate(vacancyDto),
                "createBy must be minimum 0");
    }

    @Test
    void validateWorkScheduleIsNull() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("test")
                .description("test")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .createdBy(1L)
                .count(2)
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyDtoValidator.validate(vacancyDto),
                "workSchedule must be not null");
    }

    @Test
    void validateCountIsNull() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("test")
                .description("test")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .createdBy(1L)
                .workSchedule(WorkSchedule.SHIFT_WORK)
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyDtoValidator.validate(vacancyDto),
                "count must be not null");
    }

    @Test
    void validateCountIsLessOne() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("test")
                .description("test")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .createdBy(1L)
                .workSchedule(WorkSchedule.SHIFT_WORK)
                .count(0)
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyDtoValidator.validate(vacancyDto),
                "count must be minimum 1");
    }
}