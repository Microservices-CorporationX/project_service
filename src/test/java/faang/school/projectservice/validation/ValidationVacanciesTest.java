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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void checkNotNullStringStringSuccessTest() {
        assertDoesNotThrow(() -> validationVacancies.checkNotNull("Value", "Message"));
    }

    @Test
    void checkNotNullLongStringSuccessTest() {
        assertDoesNotThrow(() -> validationVacancies.checkNotNull(1L, "Message"));
    }

    @Test
    void checkNotNullIntegerStringSuccessTest() {
        assertDoesNotThrow(() -> validationVacancies.checkNotNull(1, "Message"));
    }

    @Test
    void checkNotNullStringStringThrowFailTest() {
        assertThrows(ValidationException.class, () -> validationVacancies.checkNotNull((String) null, "Message"));
    }

    @Test
    void checkNotNullLongStringThrowFailTest() {
        assertThrows(ValidationException.class, () -> validationVacancies.checkNotNull((Long) null, "Message"));
    }

    @Test
    void checkNotNullIntegerStringThrowFailTest() {
        assertThrows(ValidationException.class, () -> validationVacancies.checkNotNull((Integer) null, "Message"));
    }

    @Test
    void checkNullListLongStringSuccessTest() {
        assertDoesNotThrow(() -> validationVacancies.checkNull((List<Long>) null, "Message"));
    }

    @Test
    void checkNullListLongStringThrowFailTest() {
        assertThrows(ValidationException.class, () -> validationVacancies.checkNull(new ArrayList<>(), "Message"));
    }

    @Test
    void checkNullLongStringSuccessTest() {
        assertDoesNotThrow(() -> validationVacancies.checkNull((Long) null, "Message"));
    }

    @Test
    void checkNullLongStringThrowFailTest() {
        assertThrows(ValidationException.class, () -> validationVacancies.checkNull(1L, "Message"));
    }

    @Test
    void projectExistSuccessTest() {
        Long idProject = 1L;
        when(projectRepository.existsById(idProject)).thenReturn(true);
        assertDoesNotThrow(() -> validationVacancies.projectExist(idProject));
    }

    @Test
    void projectExistIdNegativeThrowFailTestTest() {
        Long idProject = -1L;
        assertThrows(ValidationException.class, () -> validationVacancies.projectExist(idProject));
    }

    @Test
    void projectExistIdNotFoundThrowFailTest() {
        Long idProject = 1L;
        when(projectRepository.existsById(idProject)).thenReturn(false);
        assertThrows(ValidationException.class, () -> validationVacancies.projectExist(idProject));
    }

    @Test
    void vacancyExistSuccessTest() {
        Long idVacancy = 1L;
        when(vacancyRepository.existsById(idVacancy)).thenReturn(true);
        assertDoesNotThrow(() -> validationVacancies.vacancyExist(idVacancy));
    }

    @Test
    void vacancyExistIdNegativeThrowFailTest() {
        Long idVacancy = 0L;
        assertThrows(ValidationException.class, () -> validationVacancies.vacancyExist(idVacancy));
    }

    @Test
    void vacancyExistIdNotFoundThrowFailTest() {
        Long idVacancy = 1L;
        assertThrows(ValidationException.class, () -> validationVacancies.vacancyExist(idVacancy));
    }

    @Test
    void numberCandidatesForCloserSuccessTest() {
        List<Candidate> candidateList = List.of(new Candidate(), new Candidate(), new Candidate());
        Integer necessaryNumberCandidates = 2;
        assertDoesNotThrow(() -> validationVacancies.numberCandidatesForCloser(candidateList, necessaryNumberCandidates));
    }

    @Test
    void numberCandidatesForCloserNullCandidateListFailTest() {
        Integer numberCandidates = 3;
        assertThrows(ValidationException.class, () ->
                validationVacancies.numberCandidatesForCloser(null, numberCandidates));
    }

    @Test
    void numberCandidatesForCloserNotEnoughCandidatesFailTest() {
        List<Candidate> candidateList = List.of(new Candidate(), new Candidate());
        Integer numberCandidates = 3;
        assertThrows(ValidationException.class, () ->
                validationVacancies.numberCandidatesForCloser(candidateList, numberCandidates));
    }

    @Test
    void personHasNecessaryRoleNoTeamMemberFailTest() {
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
    void personHasNecessaryRoleNoNecessaryRolesFailTest() {
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
    void personHasNecessaryRoleSuccessTest() {
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
    void projectRepositoryExistSuccessTest() {
        when(projectRepository.existsById(1L)).thenReturn(true);
        projectRepository.existsById(1L);
        verify(projectRepository, times(1)).existsById(1L);

        ProjectRepository pr = validationVacancies.getProjectRepository();
        assertEquals(pr, projectRepository);
    }

    @Test
    void vacancyRepositoryExistSuccessTest() {
        when(vacancyRepository.existsById(1L)).thenReturn(true);
        vacancyRepository.existsById(1L);
        verify(vacancyRepository, times(1)).existsById(1L);

        VacancyRepository vr = validationVacancies.getVacancyRepository();
        assertEquals(vr, vacancyRepository);
    }

    @Test
    void teamMemberRepositoryExistSuccessTest() {
        when(teamMemberRepository.findById(1L)).thenReturn(new TeamMember());
        teamMemberRepository.findById(1L);
        verify(teamMemberRepository, times(1)).findById(1L);

        TeamMemberRepository tr = validationVacancies.getTeamMemberRepository();
        assertEquals(tr, teamMemberRepository);
    }

    @Test
    void toStringSuccessTest() {
        assertNotNull(validationVacancies.toString());
    }

    @Test
    void equalsSuccessTest() {
        ValidationVacancies v1 = new ValidationVacancies(projectRepository, vacancyRepository, teamMemberRepository);
        ValidationVacancies v2 = new ValidationVacancies(projectRepository, vacancyRepository, teamMemberRepository);
        ValidationVacancies v3 = new ValidationVacancies(null, vacancyRepository, teamMemberRepository);
        assertEquals(v1, v2);
        assertEquals(validationVacancies, validationVacancies);
        assertNotEquals(v1, v3);
        assertNotNull(validationVacancies);
        assertNotNull(validationVacancies);
    }

    @Test
    void hashCodeSuccessTest() {
        ValidationVacancies v1 = new ValidationVacancies(null, null, null);
        ValidationVacancies v2 = new ValidationVacancies(null, null, null);
        assertEquals(v1.hashCode(), v2.hashCode());
        assertNotEquals(validationVacancies.hashCode(), v2.hashCode());
    }
}