package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.utility.validator.VacancyDtoValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {
    VacancyService vacancyService = mock(VacancyService.class);
    VacancyMapper vacancyMapper = mock(VacancyMapper.class);
    UserContext userContext = mock(UserContext.class);
    VacancyDtoValidator vacancyDtoValidator = mock(VacancyDtoValidator.class);
    VacancyController vacancyController = new VacancyController(vacancyService, vacancyMapper, vacancyDtoValidator,
            userContext);

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

        when(vacancyDtoValidator.validate(vacancyDto)).thenReturn(true);
        when(userContext.getUserId()).thenReturn(1L);
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(vacancyService.createVacancy(vacancy, 1L)).thenReturn(exceptedVacancy);
        when(vacancyMapper.toDto(exceptedVacancy)).thenReturn(excepted);

        ResponseEntity<VacancyDto> actual = vacancyController.createVacancy(vacancyDto);
        Assertions.assertEquals(ResponseEntity.ok(excepted), actual);

        verify(vacancyDtoValidator, times(1)).validate(vacancyDto);
        verify(vacancyService, times(1)).createVacancy(vacancy, 1L);
        verify(userContext, times(1)).getUserId();
        verify(vacancyMapper, times(1)).toDto(exceptedVacancy);
        verify(vacancyMapper, times(1)).toEntity(vacancyDto);
    }

    @Test
    void closeVacancy() {
        when(userContext.getUserId()).thenReturn(11L);
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
                .createdBy(11L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();
        when(vacancyService.closeVacancy(1L, 11L)).thenReturn(exceptedVacancy);
        Vacancy actual = vacancyService.closeVacancy(1L, userContext.getUserId());

        Assertions.assertEquals(exceptedVacancy, actual);
        verify(userContext, times(1)).getUserId();
        verify(vacancyService, times(1)).closeVacancy(1L, 11L);
    }

}