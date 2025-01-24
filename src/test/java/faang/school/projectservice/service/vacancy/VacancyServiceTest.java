package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.candidate.CandidateService;
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
    @Mock
    private CandidateService candidateService;
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

    @Test
    void updateVacancy() {
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

        Vacancy newVacancy = Vacancy.builder()
                .name("newVacancy")
                .description("newDescription")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .build())
                .candidates(candidates)
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(4000.0)
                .workSchedule(WorkSchedule.FLEXIBLE)
                .count(4)
                .requiredSkillIds(List.of(1L, 2L, 3L, 4L))
                .build();

        Vacancy targetVacancy = Vacancy.builder()
                .id(1L)
                .name("newVacancy")
                .description("newDescription")
                .position(TeamRole.ANALYST)
                .project(Project.builder()
                        .id(1L)
                        .name("project")
                        .build())
                .candidates(candidates)
                .createdAt(LocalDateTime.of(2025, 1, 17, 15, 20))
                .createdBy(1L)
                .updatedAt(LocalDateTime.of(2025, 1, 21, 14, 30))
                .updatedBy(2L)
                .status(VacancyStatus.OPEN)
                .salary(4000.0)
                .workSchedule(WorkSchedule.FLEXIBLE)
                .count(4)
                .requiredSkillIds(List.of(1L, 2L, 3L, 4L))
                .build();

        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(sourceVacancy));
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(targetVacancy);
        doNothing().when(vacancyValidator).validateTutorRole(anyLong(), anyLong());
        doNothing().when(vacancyValidator).validateVacancyStatus(any(Vacancy.class));

        Vacancy actual = vacancyService.updateVacancy(1L, newVacancy, 2L);

        Assertions.assertEquals(targetVacancy, actual);
        verify(vacancyRepository, times(1)).save(any(Vacancy.class));
        verify(vacancyValidator, times(1)).validateTutorRole(anyLong(), anyLong());
        verify(vacancyValidator, times(1)).validateVacancyStatus(any(Vacancy.class));
        verify(vacancyRepository, times(1)).findById(anyLong());
    }

    @Test
    void deleteVacancy() {
        Vacancy vacancy = Vacancy.builder()
                .id(1L)
                .project(Project.builder()
                        .id(1L)
                        .build())
                .build();

        doNothing().when(candidateService).deleteCandidatesByVacancyId(anyLong());
        doNothing().when(vacancyRepository).deleteById(anyLong());
        doNothing().when(vacancyValidator).validateTutorRole(anyLong(), anyLong());
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(vacancy));

        Assertions.assertDoesNotThrow(() -> vacancyService.deleteVacancy(1L, 1L));
        verify(vacancyRepository, times(1)).deleteById(anyLong());
        verify(candidateService, times(1)).deleteCandidatesByVacancyId(anyLong());
    }

    @Test
    void deleteVacancyNotFound() {
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.deleteVacancy(1L, 1L),
                "vacancy 1 not found");
    }

    @Test
    void deleteVacancyIdIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.deleteVacancy(null, 1L),
                "vacancyId or tutorId is null");
    }

    @Test
    void deleteVacancyTutorIdIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.deleteVacancy(1L, null),
                "vacancyId or tutorId is null");
    }

    @Test
    void getVacancy() {
        Vacancy vacancy = Vacancy.builder()
                .id(1L)
                .build();
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
        Assertions.assertEquals(1, vacancyService.getVacancy(1L).getId());
        verify(vacancyRepository, times(1)).findById(1L);
    }

    @Test
    void getVacancyIdIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.getVacancy(null),
                "vacancyId is null");
    }

    @Test
    void getVacancyNotFound() {
        when(vacancyRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.getVacancy(1L),
                "vacancy 1 not found");
    }
}