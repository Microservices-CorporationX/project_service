package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.NotUniqueProjectException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectValidator projectValidator;

    private Project project;
    private ProjectDto projectDto;
    private UpdateProjectDto updateProjectDto;
    private Long ownerId;
    private String projectName;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        projectDto = ProjectDto.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        updateProjectDto = UpdateProjectDto.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
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
    void testValidateProjectDescriptionNotUpdatableOnTheSame() {
        assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectDescriptionUpdatable(updateProjectDto, project));
    }

    @Test
    void testValidateProjectDescriptionUpdatable() {
        updateProjectDto.setDescription("Another test description.");

        assertDoesNotThrow(() -> projectValidator.validateProjectDescriptionUpdatable(updateProjectDto, project));
    }

    @Test
    void testValidateProjectStatusNotUpdatableOnNull() {
        updateProjectDto.setStatus(null);

        assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectStatusUpdatable(updateProjectDto, project));
    }

    @Test
    void testValidateProjectStatusNotUpdatableOnTheSame() {
        assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectStatusUpdatable(updateProjectDto, project));
    }

    @Test
    void testValidateProjectStatusUpdatable() {
        updateProjectDto.setStatus(ProjectStatus.CANCELLED);

        assertDoesNotThrow(() -> projectValidator.validateProjectStatusUpdatable(updateProjectDto, project));
    }

    @Test
    void testValidateProjectVisibilityNotUpdatableOnNull() {
        updateProjectDto.setVisibility(null);

        assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectVisibilityUpdatable(updateProjectDto, project));
    }

    @Test
    void testValidateProjectVisibilityNotUpdatableOnTheSame() {
        assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectVisibilityUpdatable(updateProjectDto, project));
    }

    @Test
    void testValidateProjectVisibilityUpdatable() {
        updateProjectDto.setVisibility(ProjectVisibility.PRIVATE);

        assertDoesNotThrow(() -> projectValidator.validateProjectVisibilityUpdatable(updateProjectDto, project));
    }
}