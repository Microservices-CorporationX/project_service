package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private VacancyValidator vacancyValidator;
    @InjectMocks
    private VacancyService vacancyService;

    @Test
    void createVacancy() {
        Vacancy sourceVacancy = Vacancy.builder()
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

        Vacancy excepted = Vacancy.builder()
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

        when(projectService.getProjectById(1L)).thenReturn(Project.builder().id(1L).build());
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(excepted);
        doNothing().when(vacancyValidator).validateTutorRole(1L, 1L);

        Vacancy actual = vacancyService.createVacancy(sourceVacancy, 1L);
        verify(vacancyRepository, times(1)).save(sourceVacancy);
        Assertions.assertEquals(excepted, actual);
    }


    @Test
    void closeVacancy() {
        List<Candidate> candidates = IntStream.rangeClosed(2, 6)
                .boxed()
                .map(i -> {
                    Candidate candidate = new Candidate();
                    candidate.setUserId(Long.valueOf(i));
                    return candidate;
                })
                .toList();

        Vacancy sourceVacancy = Vacancy.builder()
                .id(1L)
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .build())
                .candidates(candidates)
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();

        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(sourceVacancy));
        Vacancy targetVacancy = Vacancy.builder()
                .id(1L)
                .name("vacancy")
                .description("description")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .build())
                .candidates(candidates)
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(1L)
                .status(VacancyStatus.CLOSED)
                .salary(3000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(5)
                .updatedAt(LocalDateTime.of(2025, 1, 21, 14, 30))
                .updatedBy(1L)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();

        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(targetVacancy);
        doNothing().when(vacancyValidator).validateVacancyStatus(any(Vacancy.class));
        doNothing().when(vacancyValidator).validateTutorRole(anyLong(), anyLong());
        doNothing().when(vacancyValidator).validateCandidatesCount(any(Vacancy.class));

        Vacancy actual = vacancyService.closeVacancy(1L, 1L);
        Assertions.assertEquals(targetVacancy, actual);

        verify(vacancyRepository, times(1)).save(any(Vacancy.class));
        verify(vacancyValidator, times(1)).validateTutorRole(anyLong(), anyLong());
        verify(vacancyValidator, times(1)).validateCandidatesCount(any(Vacancy.class));
        verify(vacancyValidator, times(1)).validateVacancyStatus(any(Vacancy.class));
        verify(vacancyRepository, times(1)).findById(anyLong());
    }

}