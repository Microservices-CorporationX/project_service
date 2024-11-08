package faang.school.projectservice.validator;

import faang.school.projectservice.dto.vacancy.NewVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.TeamMemberService;
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
    private VacancyRepository vacancyRepository;

    @Mock
    private TeamMemberService teamMemberService;

    @InjectMocks
    private VacancyValidator vacancyValidator;

    VacancyDto dto;
    NewVacancyDto newDto;
    TeamMember teamMember;

    @BeforeEach
    void setUp() {
        newDto = createTestNewVacancyDto();
        dto = createTestVacancyDto();
        teamMember = createTestTeamMember();
    }

    @Test
    @DisplayName("Check vacancy exists")
    void testValidateVacancyExistsByIdValid() {
        Long projectId = 1L;
        when(vacancyRepository.existsById(projectId)).thenReturn(true);

        assertDoesNotThrow(() -> vacancyValidator.validateVacancyExistsById(projectId));

        verify(vacancyRepository, times(1)).existsById(projectId);
    }

    @Test
    @DisplayName("Check vacancy doesn't exist")
    void testValidateVacancyExistsByIdInvalid() {
        Long projectId = 1L;
        when(vacancyRepository.existsById(projectId)).thenReturn(false);

        Exception ex = assertThrows(EntityNotFoundException.class, () -> vacancyValidator.validateVacancyExistsById(projectId));
        assertEquals("Vacancy doesn't exist by id: 1", ex.getMessage());

        verify(vacancyRepository, times(1)).existsById(projectId);
    }

    @Test
    @DisplayName("Check the vacancy manager has valid role")
    void testValidateVacancyManagerRoleValid() {
        when(teamMemberService.getTeamMemberByUserId(newDto.getCreatedBy())).thenReturn(teamMember);

        assertDoesNotThrow(() -> vacancyValidator.validateVacancyManagerRole(newDto.getCreatedBy()));

        verify(teamMemberService, times(1)).getTeamMemberByUserId(newDto.getCreatedBy());
    }

    @Test
    @DisplayName("Check the vacancy manager has invalid role")
    void testValidateVacancyManagerRoleInvalid() {
        teamMember.setRoles(List.of(TeamRole.DESIGNER));
        when(teamMemberService.getTeamMemberByUserId(newDto.getCreatedBy())).thenReturn(teamMember);

        Exception ex = assertThrows(DataValidationException.class, (() -> vacancyValidator.validateVacancyManagerRole(newDto.getCreatedBy())));
        assertEquals("Vacancy can be created by following roles " + List.of(TeamRole.OWNER, TeamRole.MANAGER), ex.getMessage());
        verify(teamMemberService, times(1)).getTeamMemberByUserId(newDto.getCreatedBy());
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

    private NewVacancyDto createTestNewVacancyDto() {
        return NewVacancyDto.builder()
                .name("Vacancy 1")
                .description("Vacancy 1 description")
                .projectId(1L)
                .createdBy(1L)
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
