package faang.school.projectservice.validator;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.InsufficientCandidatesException;
import faang.school.projectservice.model.*;
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
    TeamMember teamMember;
    Vacancy vacancy;

    @BeforeEach
    void setUp() {
        dto = createTestVacancyDto();
        teamMember = createTestTeamMember();
        vacancy = createTestVacancy();
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
    @DisplayName("Check the vacancy creator has valid role")
    void testValidateVacancyCreatorRoleValid() {
        when(teamMemberService.getTeamMemberByUserId(dto.getCreatedBy())).thenReturn(teamMember);

        assertDoesNotThrow(() -> vacancyValidator.validateVacancyCreatorRole(dto));

        verify(teamMemberService, times(1)).getTeamMemberByUserId(dto.getCreatedBy());
    }

    @Test
    @DisplayName("Check the vacancy creator has invalid role")
    void testValidateVacancyCreatorRoleInvalid() {
        teamMember.setRoles(List.of(TeamRole.DESIGNER));
        when(teamMemberService.getTeamMemberByUserId(dto.getCreatedBy())).thenReturn(teamMember);

        Exception ex = assertThrows(DataValidationException.class, (() -> vacancyValidator.validateVacancyCreatorRole(dto)));
        assertEquals("Vacancy can be created by following roles " + List.of(TeamRole.OWNER, TeamRole.MANAGER), ex.getMessage());
        verify(teamMemberService, times(1)).getTeamMemberByUserId(dto.getCreatedBy());
    }

    @Test
    @DisplayName("Check the updater of the vacancy has valid role")
    void testValidateVacancyUpdaterRoleValid(){
        when(teamMemberService.getTeamMemberById(dto.getUpdatedBy())).thenReturn(teamMember);

        assertDoesNotThrow(() -> vacancyValidator.validateVacancyUpdaterRole(dto));
        verify(teamMemberService, times(1)).getTeamMemberById(dto.getUpdatedBy());
    }

    @Test
    @DisplayName("Check the updater of the vacancy has invalid role")
    void testValidateVacancyUpdaterRoleInvalid(){
        teamMember.setRoles(List.of(TeamRole.ANALYST));
        when(teamMemberService.getTeamMemberById(dto.getUpdatedBy())).thenReturn(teamMember);

        Exception ex = assertThrows(DataValidationException.class, () -> vacancyValidator.validateVacancyUpdaterRole(dto));
        assertEquals("Vacancy status can be modified by following roles " + List.of(TeamRole.OWNER, TeamRole.MANAGER), ex.getMessage());
        verify(teamMemberService, times(1)).getTeamMemberById(dto.getUpdatedBy());
    }

    @Test
    @DisplayName("Check amount of candidates to close the vacancy equal to limit")
    void testValidateCandidateCountForClosureCandidatesEqualToLimit() {
        vacancy.setCount(3);
        vacancy.setCandidates(List.of(new Candidate(), new Candidate(), new Candidate()));

        assertDoesNotThrow(() -> vacancyValidator.validateCandidateCountForClosure(vacancy));
    }

    @Test
    @DisplayName("Check amount of candidates to close the vacancy is more than limit")
    void testValidateCandidateCountForClosureCandidatesMoreThanLimit() {
        vacancy.setCount(2);
        vacancy.setCandidates(List.of(new Candidate(), new Candidate(), new Candidate()));

        assertDoesNotThrow(() -> vacancyValidator.validateCandidateCountForClosure(vacancy));
    }

    @Test
    @DisplayName("Check amount of candidates to close the vacancy is less than limit")
    void testValidateCandidateCountForClosureCandidatesLessThanLimit() {
        vacancy.setCount(4);
        vacancy.setCandidates(List.of(new Candidate(), new Candidate(), new Candidate()));

        Exception ex = assertThrows(InsufficientCandidatesException.class,
                () -> vacancyValidator.validateCandidateCountForClosure(vacancy));
        assertEquals("Vacancy should have at least 4 candidates to be closed", ex.getMessage());
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

    private Vacancy createTestVacancy() {
        return Vacancy.builder()
                .id(1L)
                .name("Vacancy 1")
                .description("Vacancy 1 description")
                .status(VacancyStatus.OPEN)
                .candidates(List.of(new Candidate()))
                .project(Project.builder().id(1L).build())
                .createdAt(LocalDateTime.now())
                .salary(100.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(1L))
                .build();
    }
}
