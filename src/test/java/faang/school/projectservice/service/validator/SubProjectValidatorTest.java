package faang.school.projectservice.service.validator;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SubProjectValidatorTest {


    private static final long ID = 1L;
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private SubProjectValidator subProjectValidator;

    private CreateSubProjectDto createFailingDto;
    private CreateSubProjectDto createValidDto;

    @BeforeEach
    public void beforeEach() {
        createFailingDto = new CreateSubProjectDto();
        createFailingDto.setParentProjectId(ID);
        createFailingDto.setVisibility(ProjectVisibility.PUBLIC);

        createValidDto = new CreateSubProjectDto();
        createValidDto.setParentProjectId(ID);
        createValidDto.setVisibility(ProjectVisibility.PUBLIC);

    }

    @Test
    public void testSubProjectCreationWithNonExistingParent() {
        Mockito.when(projectRepository.findById(createFailingDto.getParentProjectId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            subProjectValidator.validateSubProjectCreation(createFailingDto);
        });
    }

    @Test
    public void testSubProjectCreationWithExistParent() {
        Project parentProject = Project.builder().parentProject(new Project()).build();

        Mockito.when(projectRepository.findById(createFailingDto.getParentProjectId())).thenReturn(Optional.ofNullable(parentProject));

        assertThrows(BusinessException.class, () -> {
            subProjectValidator.validateSubProjectCreation(createFailingDto);
        });
    }

    @Test
    public void testSubProjectCreationWithPublicVisibility() {
        Project parentProject = Project.builder().parentProject(null).visibility(ProjectVisibility.PRIVATE).build();

        Mockito.when(projectRepository.findById(createFailingDto.getParentProjectId())).thenReturn(Optional.ofNullable(parentProject));

        assertThrows(BusinessException.class, () -> {
            subProjectValidator.validateSubProjectCreation(createFailingDto);
        });
    }

    @Test
    public void testSubProjectCreationSuccessCase() {
        Project parentProject = Project.builder().parentProject(null).visibility(ProjectVisibility.PUBLIC).build();

        Mockito.when(projectRepository.findById(createFailingDto.getParentProjectId())).thenReturn(Optional.ofNullable(parentProject));

        assertDoesNotThrow(() -> subProjectValidator.validateSubProjectCreation(createValidDto));
    }

    @Test
    public void testSubProjectsStatusesAreDifferent() {
        Project subproject1 = Project.builder().status(ProjectStatus.IN_PROGRESS).build();
        Project subproject2 = Project.builder().status(ProjectStatus.COMPLETED).build();
        ProjectStatus parentStatus = ProjectStatus.COMPLETED;

        List<Project> subProjects = List.of(subproject1, subproject2);

        assertThrows(BusinessException.class, () -> {
            subProjectValidator.validateSubProjectStatuses(subProjects, parentStatus);
        });
    }

    @Test
    public void testSubProjectsStatusesAreSame() {
        Project subproject1 = Project.builder().status(ProjectStatus.COMPLETED).build();
        Project subproject2 = Project.builder().status(ProjectStatus.COMPLETED).build();
        ProjectStatus parentStatus = ProjectStatus.COMPLETED;

        List<Project> subProjects = List.of(subproject1, subproject2);

        assertDoesNotThrow(() -> subProjectValidator.validateSubProjectStatuses(subProjects, parentStatus));
    }
}
