package faang.school.projectservice.validator.moment;

import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.exception.TeamMemberNotFoundException;
import faang.school.projectservice.exception.vacancy.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MomentServiceValidatorTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @InjectMocks
    private MomentServiceValidator validator;

    @Test
    void testValidateProjectsExistProjectSuccess() {
        List<Long> projectIds = List.of(1L, 2L);
        when(projectRepository.existsById(anyLong())).thenReturn(true);
        validator.validateProjectsExist(projectIds);
        verify(projectRepository, times(2)).existsById(anyLong());
    }

    @Test
    void testValidateProjectsExistProjectNotFound() {
        List<Long> projectIds = List.of(1L, 2L);
        when(projectRepository.existsById(1L)).thenReturn(true);

        ProjectNotFoundException exception = assertThrows(ProjectNotFoundException.class,
                () -> validator.validateProjectsExist(projectIds));
        Assertions.assertEquals("Project not found with ID: 2", exception.getMessage());
    }

    @Test
    void validateProjectsAreActiveSuccess() {
        List<Long> projectIds = List.of(1L, 2L);
        TeamMember member = TeamMember.builder().id(11L).build();
        Team team = Team.builder().teamMembers(List.of(member)).build();
        Project project1 = Project.builder().id(1L).status(ProjectStatus.CREATED).teams(List.of(team)).build();
        Project project2 = Project.builder().id(1L).status(ProjectStatus.IN_PROGRESS).teams(List.of(team)).build();
        when(projectRepository.getProjectById(1L)).thenReturn(project1);
        when(projectRepository.getProjectById(2L)).thenReturn(project2);

        validator.validateProjectsAreActive(projectIds);
        verify(projectRepository, times(2)).getProjectById(anyLong());
    }

    @Test
    void validateProjectsAreActiveInactiveProject() {
        List<Long> projectIds = List.of(1L, 2L);
        TeamMember member = TeamMember.builder().id(11L).build();
        Team team = Team.builder().teamMembers(List.of(member)).build();
        Project project = Project.builder().id(1L).status(ProjectStatus.COMPLETED).teams(List.of(team)).build();
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> validator.validateProjectsAreActive(projectIds));
        Assertions.assertEquals("Project is not active. Current status for project with ID: 1 is: COMPLETED Following team mates belong to this project and therefore cannot be added to the moment : [11]", exception.getMessage());
    }

    @Test
    void testValidateListContainUniqueItemsDuplicates() {
        List<Long> ids = List.of(1L, 2L, 2L);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validateListContainUniqueItems(ids, "Project List"));
        Assertions.assertEquals("List: Project List has duplicate items", exception.getMessage());
    }

    @Test
    void testValidateTeamMemberExistsSuccess() {
        List<Long> teamIds = List.of(1L, 2L, 3L);
        when(teamMemberRepository.findById(anyLong())).thenReturn(new TeamMember());
        validator.validateTeamMemberExists(teamIds);
        verify(teamMemberRepository, times(3)).findById(anyLong());
    }

    @Test
    void testValidateTeamMemberExistsNotFound() {
        List<Long> teamIds = List.of(1L, 2L, 3L);
        when(teamMemberRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);
        TeamMemberNotFoundException exception = assertThrows(TeamMemberNotFoundException.class,
                () -> validator.validateTeamMemberExists(teamIds));
        Assertions.assertEquals("Team member not found: 1", exception.getMessage());
    }
}
