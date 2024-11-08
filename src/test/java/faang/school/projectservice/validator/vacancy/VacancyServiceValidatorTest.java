package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.client.vacancy.VacancyDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.team.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class VacancyServiceValidatorTest {

    @Mock
    private TeamService teamService;

    @InjectMocks
    private VacancyServiceValidator vacancyServiceValidator;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(teamService.findMemberByUserIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(getTeamMember()));
    }

    @Test
    void validateCreateVacancy_noNeededRoles() {
        TeamMember teamMember = getTeamMember();
        teamMember.setRoles(List.of());

        Mockito.lenient().when(teamService.findMemberByUserIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(teamMember));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vacancyServiceValidator.validateCreateVacancy(getVacancyDto()));
        assertEquals("Team member id %s dont have needed role".formatted(getVacancyDto().getCreatedBy()), exception.getMessage());
    }

    @Test
    void validateCreateVacancySuccess() {
        assertDoesNotThrow(() -> vacancyServiceValidator.validateCreateVacancy(getVacancyDto()));
    }

    @Test
    void validateCloseVacancy_notEnoughCandidates() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vacancyServiceValidator.validateCloseVacancy(getVacancyDto()));
        assertEquals("Vacancy id %s count is greater than candidate count".formatted(getVacancyDto().getId()), exception.getMessage());
    }

    @Test
    void validateCloseVacancySuccess() {
        VacancyDto vacancyDto = getVacancyDto();
        vacancyDto.setCount(2);

        assertDoesNotThrow(() -> vacancyServiceValidator.validateCloseVacancy(vacancyDto));
    }

    private VacancyDto getVacancyDto() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setCreatedBy(1L);
        vacancyDto.setProjectId(1L);
        vacancyDto.setCandidateIds(List.of(1L, 2L, 3L));
        vacancyDto.setCount(5);
        return vacancyDto;
    }

    private TeamMember getTeamMember() {
        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.OWNER));
        return teamMember;
    }
}