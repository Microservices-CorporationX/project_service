package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.NotUniqueProjectException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        Long projectId = 1L;
        when(projectRepository.existsById(projectId)).thenReturn(true);

        assertDoesNotThrow(() -> projectValidator.validateProjectExistsById(projectId));

        verify(projectRepository, times(1)).existsById(projectId);
    }

    @Test
    @DisplayName("Check project doesn't exist")
    void testValidateProjectInVacancyNotExists() {
        Long projectId = 1L;
        when(projectRepository.existsById(projectId)).thenReturn(false);

        Exception ex = assertThrows(ProjectNotFoundException.class, () -> projectValidator.validateProjectExistsById(projectId));
        assertEquals("Project with id 1 doesn't exist", ex.getMessage());

        verify(projectRepository, times(1)).existsById(projectId);
    }

    @Test
    public void testIsOpenProjectWhenStatusInProgress() {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        assertTrue(projectValidator.isOpenProject(projectId));
    }

    @Test
    public void testIsOpenProjectWhenStatusCompleted() {
        project.setStatus(ProjectStatus.COMPLETED);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        assertFalse(projectValidator.isOpenProject(projectId));
    }

    @Test
    public void testIsOpenProjectWhenStatusCancelled() {
        project.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        assertFalse(projectValidator.isOpenProject(projectId));
    }


    @Test
    public void testCheckUserIsProjectOwner() {
        Long currentUserId = 1L;
        project.setOwnerId(currentUserId);
        assertDoesNotThrow(() -> projectValidator.checkUserIsProjectOwner(currentUserId, project));
    }
}