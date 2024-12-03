package faang.school.projectservice.validation;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationVacanciesTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private ValidationVacancies validationVacancies;

    @Test
    void testHasToBeNotNullStringStringSuccess() {
        assertDoesNotThrow(() -> validationVacancies.hasToBeNotNull("Value", "Message"));
    }

    @Test
    void testHasToBeNotNullLongStringSuccess() {
        assertDoesNotThrow(() -> validationVacancies.hasToBeNotNull(1L, "Message"));
    }

    @Test
    void testHasToBeNotNullIntegerStringSuccess() {
        assertDoesNotThrow(() -> validationVacancies.hasToBeNotNull(1, "Message"));
    }

    @Test
    void testHasToBeNotNullStringStringThrowFail() {
        assertThrows(ValidationException.class, () -> validationVacancies.hasToBeNotNull((String) null, "Message"));
    }

    @Test
    void testHasToBeNotNullLongStringThrowFail() {
        assertThrows(ValidationException.class, () -> validationVacancies.hasToBeNotNull((Long) null, "Message"));
    }

    @Test
    void testHasToBeNotNullIntegerStringThrowFail() {
        assertThrows(ValidationException.class, () -> validationVacancies.hasToBeNotNull((Integer) null, "Message"));
    }

    @Test
    void testHasToBeNullListLongStringSuccess() {
        assertDoesNotThrow(() -> validationVacancies.hasToBeNull((List<Long>) null, "Message"));
    }

    @Test
    void testHasToBeNullListLongStringThrowFail() {
        assertThrows(ValidationException.class, () -> validationVacancies.hasToBeNull(new ArrayList<>(), "Message"));
    }

    @Test
    void testHasToBeNullLongStringSuccess() {
        assertDoesNotThrow(() -> validationVacancies.hasToBeNull((Long) null, "Message"));
    }

    @Test
    void testHasToBeNullLongStringThrowFail() {
        assertThrows(ValidationException.class, () -> validationVacancies.hasToBeNull(1L, "Message"));
    }

    @Test
    void testIsProjectExistSuccess() {
        Long idProject = 1L;
        when(projectRepository.existsById(idProject)).thenReturn(true);
        assertDoesNotThrow(() -> validationVacancies.isProjectExist(idProject));
    }

    @Test
    void testIsProjectExistIdNegativeThrowFail() {
        Long idProject = -1L;
        assertThrows(ValidationException.class, () -> validationVacancies.isProjectExist(idProject));
    }

    @Test
    void testIsProjectExistIdNotFoundThrowFail() {
        Long idProject = 1L;
        when(projectRepository.existsById(idProject)).thenReturn(false);
        assertThrows(ValidationException.class, () -> validationVacancies.isProjectExist(idProject));
    }

    @Test
    void testIsVacancyExistSuccess() {
        Long idVacancy = 1L;
        when(vacancyRepository.existsById(idVacancy)).thenReturn(true);
        assertDoesNotThrow(() -> validationVacancies.isVacancyExist(idVacancy));
    }

    @Test
    void testIsVacancyExistIdNegativeThrowFail() {
        Long idVacancy = 0L;
        assertThrows(ValidationException.class, () -> validationVacancies.isVacancyExist(idVacancy));
    }

    @Test
    void testIsVacancyExistIdNotFoundThrowFail() {
        Long idVacancy = 1L;
        assertThrows(ValidationException.class, () -> validationVacancies.isVacancyExist(idVacancy));
    }

    @Test
    void testNumberCandidatesForCloserSuccess() {
        List<Candidate> candidateList = List.of(new Candidate(), new Candidate(), new Candidate());
        Integer necessaryNumberCandidates = 2;
        assertDoesNotThrow(() -> validationVacancies.numberCandidatesForCloser(candidateList, necessaryNumberCandidates));
    }

    @Test
    void testNumberCandidatesForCloserNullCandidateListFail() {
        Integer numberCandidates = 3;
        assertThrows(ValidationException.class, () ->
                validationVacancies.numberCandidatesForCloser(null, numberCandidates));
    }

    @Test
    void testNumberCandidatesForCloserNotEnoughCandidatesFail() {
        List<Candidate> candidateList = List.of(new Candidate(), new Candidate());
        Integer numberCandidates = 3;
        assertThrows(ValidationException.class, () ->
                validationVacancies.numberCandidatesForCloser(candidateList, numberCandidates));
    }

    @Test
    void testPersonHasNecessaryRoleNoTeamMemberFail() {
        long idPersonBy = 1L;
        long projectId = 1L;
        String nameUser = "User1";

        when(teamMemberRepository.getJpaRepository()).thenReturn(teamMemberJpaRepository);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(idPersonBy, projectId))
                .thenReturn(null);
        assertThrows(ValidationException.class, () ->
                validationVacancies.personHasNecessaryRole(idPersonBy, nameUser, projectId));
    }

    @Test
    void testPersonHasNecessaryRoleNoNecessaryRolesFail() {
        long idPersonBy = 1L;
        long projectId = 1L;
        String nameUser = "User2";

        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.DEVELOPER));

        when(teamMemberRepository.getJpaRepository()).thenReturn(teamMemberJpaRepository);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(idPersonBy, projectId))
                .thenReturn(teamMember);

        assertThrows(ValidationException.class, () ->
                validationVacancies.personHasNecessaryRole(idPersonBy, nameUser, projectId));
    }

    @Test
    void testPersonHasNecessaryRoleSuccess() {
        long idPersonBy = 1L;
        long projectId = 1L;
        List<TeamRole> tpmTeamRoleList = List.of(TeamRole.MANAGER, TeamRole.DESIGNER);

        Team team = new Team();
        List<Stage> stageList = new ArrayList<>();
        TeamMember tmpTeamMember = new TeamMember(1L, 1L, tpmTeamRoleList, team, stageList);

        when(teamMemberRepository.getJpaRepository()).thenReturn(teamMemberJpaRepository);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(idPersonBy, projectId)).thenReturn(tmpTeamMember);
        assertDoesNotThrow(() -> validationVacancies.personHasNecessaryRole(idPersonBy, "Message", projectId));
    }

    @Test
    void testProjectRepositoryExistSuccess() {
        when(projectRepository.existsById(1L)).thenReturn(true);
        projectRepository.existsById(1L);
        verify(projectRepository, times(1)).existsById(1L);

        ProjectRepository pr = validationVacancies.getProjectRepository();
        assertEquals(pr, projectRepository);
    }

    @Test
    void testVacancyRepositoryExistSuccess() {
        when(vacancyRepository.existsById(1L)).thenReturn(true);
        vacancyRepository.existsById(1L);
        verify(vacancyRepository, times(1)).existsById(1L);

        VacancyRepository vr = validationVacancies.getVacancyRepository();
        assertEquals(vr, vacancyRepository);
    }

    @Test
    void testTeamMemberRepositoryExistSuccess() {
        when(teamMemberRepository.findById(1L)).thenReturn(new TeamMember());
        teamMemberRepository.findById(1L);
        verify(teamMemberRepository, times(1)).findById(1L);

        TeamMemberRepository tr = validationVacancies.getTeamMemberRepository();
        assertEquals(tr, teamMemberRepository);
    }

    @Test
    void testToStringSuccess() {
        assertNotNull(validationVacancies.toString());
    }

    @Test
    void testEqualsSuccess() {
        ValidationVacancies v1 = new ValidationVacancies(projectRepository, vacancyRepository, teamMemberRepository);
        ValidationVacancies v2 = new ValidationVacancies(projectRepository, vacancyRepository, teamMemberRepository);
        ValidationVacancies v3 = new ValidationVacancies(null, vacancyRepository, teamMemberRepository);
        assertTrue(v1.equals(v2));
        assertTrue(validationVacancies.equals(validationVacancies));
        assertFalse(v1.equals(v3));
        assertFalse(validationVacancies.equals(null));
        assertFalse(validationVacancies.equals(new Object()));
    }

    @Test
    void testHashCodeSuccess() {
        ValidationVacancies v1 = new ValidationVacancies(null, null, null);
        ValidationVacancies v2 = new ValidationVacancies(null, null, null);
        assertEquals(v1.hashCode(), v2.hashCode());
        assertNotEquals(validationVacancies.hashCode(), v2.hashCode());
    }
}