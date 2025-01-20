package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private TeamMemberService teamMemberService;
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

        Mockito.when(projectService.getProjectById(1L)).thenReturn(Project.builder().id(1L).build());
        Mockito.when(vacancyRepository.save(Mockito.any(Vacancy.class))).thenReturn(excepted);

        Mockito.when(teamMemberService.getTeamMemberByIdAndProjectId(1L, 1L))
                .thenReturn(TeamMember.builder()
                        .id(1L)
                        .userId(1L)
                        .team(Team.builder()
                                .id(1L)
                                .project(Project.builder()
                                        .id(1L)
                                        .build())
                                .build())
                        .roles(List.of(TeamRole.MANAGER))
                        .build()
                );

        Vacancy actual = vacancyService.createVacancy(sourceVacancy);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(sourceVacancy);
        Assertions.assertEquals(excepted, actual);
    }

    @Test
    void createVacancyWithNOtRole() {
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

        Mockito.when(projectService.getProjectById(1L)).thenReturn(Project.builder().id(1L).build());

        Mockito.when(teamMemberService.getTeamMemberByIdAndProjectId(1L, 1L))
                .thenReturn(TeamMember.builder()
                        .id(1L)
                        .userId(1L)
                        .team(Team.builder()
                                .id(1L)
                                .project(Project.builder()
                                        .id(1L)
                                        .build())
                                .build())
                        .roles(List.of(TeamRole.DESIGNER, TeamRole.DEVELOPER))
                        .build()
                );

        Assertions.assertThrows(DataValidationException.class, () -> vacancyService.createVacancy(sourceVacancy),
                "1 user does not have permission to add a vacancy");
        Mockito.verify(vacancyRepository, Mockito.times(0)).save(sourceVacancy);
    }
}