package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.utility.validator.VacancyDtoValidator;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {
    VacancyService vacancyService = Mockito.mock(VacancyService.class);
    VacancyMapper vacancyMapper = Mockito.mock(VacancyMapper.class);
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    VacancyDtoValidator vacancyDtoValidator = new VacancyDtoValidator(validator);
    VacancyController vacancyController = new VacancyController(vacancyService, vacancyMapper, vacancyDtoValidator);

    @Test
    void createVacancy() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .createdBy(1L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();

        Vacancy vacancy = Vacancy.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .build())
                .candidates(List.of())
                .createdBy(1L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();

        Vacancy exceptedVacancy = Vacancy.builder()
                .id(1L)
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .build())
                .candidates(List.of())
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();

        VacancyDto excepted = VacancyDto.builder()
                .id(1L)
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .createdBy(1L)
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .status(VacancyStatus.OPEN)
                .candidatesId(List.of())
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();
        Mockito.when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        Mockito.when(vacancyService.createVacancy(vacancy)).thenReturn(exceptedVacancy);
        Mockito.when(vacancyMapper.toDto(exceptedVacancy)).thenReturn(excepted);
        ResponseEntity<VacancyDto> actual = vacancyController.createVacancy(vacancyDto);
        Assertions.assertEquals(ResponseEntity.ok(excepted), actual);
        Mockito.verify(vacancyService, Mockito.times(1)).createVacancy(vacancy);
        Mockito.verify(vacancyMapper, Mockito.times(1)).toDto(exceptedVacancy);
        Mockito.verify(vacancyMapper, Mockito.times(1)).toEntity(vacancyDto);
    }

    @Test
    void createVacancyWithBlankName() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("")
                .description("description")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .createdBy(1L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    void createVacancyWithBlankDescription() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("vacancy")
                .description("")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .createdBy(1L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    void createVacancyWithNullPosition() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("vacancy")
                .description("description")
                .projectId(1L)
                .createdBy(1L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    void createVacancyWithIncorrectProjectId() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .projectId(-1L)
                .createdBy(1L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    void createVacancyWithNullProjectId() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .createdBy(1L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    void createVacancyWithIncorrectCreatedBy() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .createdBy(-1L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    void createVacancyWithNullCreatedBy() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    void createVacancyWithNullWorkSchedule() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .createdBy(1L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    void createVacancyWithIncorrectCount() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .projectId(1L)
                .createdBy(1L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(0)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    void createVacancyWithNullCount() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .projectId(-1L)
                .createdBy(1L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();
        Assertions.assertThrows(DataValidationException.class, () -> vacancyController.createVacancy(vacancyDto));
    }

}