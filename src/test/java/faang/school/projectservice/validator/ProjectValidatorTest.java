package faang.school.projectservice.validator;

import faang.school.projectservice.dto.ProjectDto;
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

    private Long id;
    private Long ownerId;
    private String projectName;
    private ProjectDto projectDto;
    private Project project;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        project = Project.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        id = projectDto.getId();
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
        when(projectRepository.getProjectById(id)).thenReturn(project);

        assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectDescriptionUpdatable(projectDto));
    }

    @Test
    void testValidateProjectDescriptionUpdatable() {
        projectDto.setDescription("Another test description.");
        when(projectRepository.getProjectById(id)).thenReturn(project);

        assertDoesNotThrow(() -> projectValidator.validateProjectDescriptionUpdatable(projectDto));
    }

    @Test
    void testValidateProjectStatusNotUpdatableOnNull() {
        projectDto.setStatus(null);
        when(projectRepository.getProjectById(id)).thenReturn(project);

        assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectStatusUpdatable(projectDto));
    }

    @Test
    void testValidateProjectStatusNotUpdatableOnTheSame() {
        when(projectRepository.getProjectById(id)).thenReturn(project);

        assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectStatusUpdatable(projectDto));
    }

    @Test
    void testValidateProjectStatusUpdatable() {
        projectDto.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.getProjectById(id)).thenReturn(project);

        assertDoesNotThrow(() -> projectValidator.validateProjectStatusUpdatable(projectDto));
    }

    @Test
    void testValidateProjectVisibilityNotUpdatableOnNull() {
        projectDto.setVisibility(null);
        when(projectRepository.getProjectById(id)).thenReturn(project);

        assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectVisibilityUpdatable(projectDto));
    }

    @Test
    void testValidateProjectVisibilityNotUpdatableOnTheSame() {
        when(projectRepository.getProjectById(id)).thenReturn(project);

        assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectVisibilityUpdatable(projectDto));
    }

    @Test
    void testValidateProjectVisibilityUpdatable() {
        projectDto.setVisibility(ProjectVisibility.PRIVATE);
        when(projectRepository.getProjectById(id)).thenReturn(project);

        assertDoesNotThrow(() -> projectValidator.validateProjectVisibilityUpdatable(projectDto));
    }
}