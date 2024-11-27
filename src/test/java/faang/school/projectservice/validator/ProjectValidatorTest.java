package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.NotUniqueProjectException;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectValidator projectValidator;

    private ProjectDto projectDto;
    private Project project;
    private Long ownerId;
    private String projectName;
    private Long projectId = 1L;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        project = Project.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        projectName = projectDto.getName();
        ownerId = projectDto.getOwnerId();
    }

    @Test
    void testValidateUniqueProjectFailed() {
        when(projectRepository.existsByOwnerUserIdAndName(ownerId, projectName)).thenReturn(true);

        assertThrows(NotUniqueProjectException.class,
                () -> projectValidator.validateUniqueProject(projectDto));
    }

    @Test
    void testValidateUniqueProjectSuccess() {
        when(projectRepository.existsByOwnerUserIdAndName(ownerId, projectName)).thenReturn(false);

        assertDoesNotThrow(() -> projectValidator.validateUniqueProject(projectDto));
    }

    @Test
    void testUserCanAccessPrivateProject() {
        assertTrue(projectValidator.canUserAccessProject(project, ownerId));
    }

    @Test
    void testUserCanNotAccessPrivateProject() {
        project.setOwnerId(2L);
        assertFalse(projectValidator.canUserAccessProject(project, ownerId));
    }

    @Test
    void testUserCanAccessPublicProject() {
        project.setOwnerId(2L);
        project.setVisibility(ProjectVisibility.PUBLIC);
        assertTrue(projectValidator.canUserAccessProject(project, ownerId));
    }

    @Test
    @DisplayName("Check project exists")
    void testValidateProjectExistsById() {
        when(projectRepository.existsById(projectId)).thenReturn(true);

        assertDoesNotThrow(() -> projectValidator.validateProjectExistsById(projectId));

        verify(projectRepository, times(1)).existsById(projectId);
    }

    @Test
    @DisplayName("Check project doesn't exist")
    void testValidateProjectInVacancyNotExists() {
        when(projectRepository.existsById(projectId)).thenReturn(false);

        Exception ex = assertThrows(EntityNotFoundException.class, () -> projectValidator.validateProjectExistsById(projectId));
        assertEquals("Project with id 1 doesn't exist", ex.getMessage());

        verify(projectRepository, times(1)).existsById(projectId);
    }

    @Test
    void testIsOpenProjectWhenStatusInProgress() {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        assertTrue(projectValidator.isOpenProject(projectId));
    }

    @Test
    void testIsOpenProjectWhenStatusCompleted() {
        project.setStatus(ProjectStatus.COMPLETED);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        assertFalse(projectValidator.isOpenProject(projectId));
    }

    @Test
    void testIsOpenProjectWhenStatusCancelled() {
        project.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        assertFalse(projectValidator.isOpenProject(projectId));
    }

    @Test
    @DisplayName("Test user belongs to the project team by valid userId: success")
    void validateUserInProjectTeam_ValidParameters_Success(){
        TeamMember teamMember = TeamMember.builder().userId(1L).build();
        Team team = Team.builder().teamMembers(List.of(teamMember)).build();
        project.setTeams(List.of(team));

        assertDoesNotThrow(() -> projectValidator.validateUserInProjectTeam(1L, project));
    }

    @Test
    @DisplayName("Test user doesn't belong to the project team: fail")
    void validateUserInProjectTeam_ValidParametersNoTeam_FailException(){
        TeamMember teamMember = TeamMember.builder().userId(2L).build();
        Team team = Team.builder().teamMembers(List.of(teamMember)).build();
        project.setId(5L);
        project.setTeams(List.of(team));

        Exception ex = assertThrows(EntityNotFoundException.class, () -> projectValidator.validateUserInProjectTeam(1L, project));
        assertEquals(String.format("User id: 1 doesn't work on project id: %d", project.getId()), ex.getMessage());
    }

    @Test
    @DisplayName("Test project doesn't have a team: fail")
    void validateUserInProjectTeam_ProjectWithoutAnyTeam_FailException(){
        project.setId(5L);
        project.setTeams(List.of());

        Exception ex = assertThrows(EntityNotFoundException.class, () -> projectValidator.validateUserInProjectTeam(1L, project));
        assertEquals(String.format("User id: 1 doesn't work on project id: %d", project.getId()), ex.getMessage());
    }
}