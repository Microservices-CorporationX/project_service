package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyValidatorTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private TeamMemberService teamMemberService;

    @InjectMocks
    private VacancyValidator vacancyValidator;

    VacancyDto dto;
    TeamMember teamMember;

    @BeforeEach
    void setUp() {
        dto = createTestVacancyDto();
        teamMember = createTestTeamMember();
    }

    @Test
    @DisplayName("Check project in vacancy exists")
    void validateProjectInVacancyExists() {
        when(projectService.checkProjectExistsById(dto.getProjectId())).thenReturn(true);

        assertDoesNotThrow(() -> vacancyValidator.validateProjectInVacancyExists(dto));

        verify(projectService, times(1)).checkProjectExistsById(dto.getProjectId());
    }

    @Test
    @DisplayName("Check project in vacancy doesn't exist")
    void validateProjectInVacancyNotExists() {
        when(projectService.checkProjectExistsById(dto.getProjectId())).thenReturn(false);

        Exception ex = assertThrows(EntityNotFoundException.class, () -> vacancyValidator.validateProjectInVacancyExists(dto));
        assertEquals("Project doesn't exist by id: 1", ex.getMessage());

        verify(projectService, times(1)).checkProjectExistsById(dto.getProjectId());
    }

    @Test
    @DisplayName("Check the vacancy creator has valid role")
    void validateVacancyCreatorRoleValid() {
        when(teamMemberService.getTeamMemberById(dto.getCreatedBy())).thenReturn(teamMember);

        assertDoesNotThrow(() -> vacancyValidator.validateVacancyCreatorRole(dto));

        verify(teamMemberService, times(1)).getTeamMemberById(dto.getCreatedBy());
    }

    @Test
    @DisplayName("Check the vacancy creator has invalid role")
    void validateVacancyCreatorRoleInvalid() {
        teamMember.setRoles(List.of(TeamRole.DESIGNER));
        when(teamMemberService.getTeamMemberById(dto.getCreatedBy())).thenReturn(teamMember);

        Exception ex = assertThrows(DataValidationException.class, (() -> vacancyValidator.validateVacancyCreatorRole(dto)));
        assertEquals("Vacancy can be created by following roles " + List.of(TeamRole.OWNER, TeamRole.MANAGER), ex.getMessage());
        verify(teamMemberService, times(1)).getTeamMemberById(dto.getCreatedBy());
    }

    private VacancyDto createTestVacancyDto() {
        return VacancyDto.builder()
                .id(1L)
                .name("Vacancy 1")
                .description("Vacancy 1 description")
                .projectId(1L)
                .createdAt(LocalDateTime.now())
                .salary(100.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(1L))
                .build();
    }

    private TeamMember createTestTeamMember() {
        return TeamMember.builder()
                .id(1L)
                .roles(List.of(TeamRole.OWNER))
                .build();
    }
}
