package faang.school.projectservice.service.validator;

import faang.school.projectservice.dto.project.SubProjectCreateDto;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {


    private static final long ID = 1L;
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectValidator projectValidator;

    private SubProjectCreateDto createFailingDto;
    private SubProjectCreateDto createValidDto;

    @BeforeEach
    public void beforeEach() {
        createFailingDto = new SubProjectCreateDto();
        createFailingDto.setParentProjectId(ID);
        createFailingDto.setVisibility(ProjectVisibility.PUBLIC);

        createValidDto = new SubProjectCreateDto();
        createValidDto.setParentProjectId(ID);
        createValidDto.setVisibility(ProjectVisibility.PUBLIC);

    }

    @Test
    public void testSubProjectCreationWithNonExistingParent() {
        Mockito.when(projectRepository.findById(createFailingDto.getParentProjectId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            projectValidator.validateSubProjectCreation(createFailingDto);
        });
    }

    @Test
    public void testSubProjectCreationWithExistParent() {
        Project parentProject = Project.builder().parentProject(new Project()).build();

        Mockito.when(projectRepository.findById(createFailingDto.getParentProjectId())).thenReturn(Optional.ofNullable(parentProject));

        assertThrows(BusinessException.class, () -> {
            projectValidator.validateSubProjectCreation(createFailingDto);
        });
    }

    @Test
    public void testSubProjectCreationWithPublicVisibility() {
        Project parentProject = Project.builder().parentProject(null).visibility(ProjectVisibility.PRIVATE).build();

        Mockito.when(projectRepository.findById(createFailingDto.getParentProjectId())).thenReturn(Optional.ofNullable(parentProject));

        assertThrows(BusinessException.class, () -> {
            projectValidator.validateSubProjectCreation(createFailingDto);
        });
    }

    @Test
    public void testSubProjectCreationSuccessCase() {
        Project parentProject = Project.builder().parentProject(null).visibility(ProjectVisibility.PUBLIC).build();

        Mockito.when(projectRepository.findById(createFailingDto.getParentProjectId())).thenReturn(Optional.ofNullable(parentProject));

        assertDoesNotThrow(() -> projectValidator.validateSubProjectCreation(createValidDto));
    }

    @Test
    public void testSubProjectsStatusesAreDifferent() {
        Project subproject1 = Project.builder().status(ProjectStatus.IN_PROGRESS).build();
        Project subproject2 = Project.builder().status(ProjectStatus.COMPLETED).build();
        ProjectStatus parentStatus = ProjectStatus.COMPLETED;

        List<Project> subProjects = List.of(subproject1, subproject2);

        assertThrows(BusinessException.class, () -> {
            projectValidator.validateSubProjectStatuses(subProjects, parentStatus);
        });
    }

    @Test
    public void testSubProjectsStatusesAreSame() {
        Project subproject1 = Project.builder().status(ProjectStatus.COMPLETED).build();
        Project subproject2 = Project.builder().status(ProjectStatus.COMPLETED).build();
        ProjectStatus parentStatus = ProjectStatus.COMPLETED;

        List<Project> subProjects = List.of(subproject1, subproject2);

        assertDoesNotThrow(() -> projectValidator.validateSubProjectStatuses(subProjects, parentStatus));
    }

    @Test
    public void testApplyPrivateVisibilityIfParentIsPublic() {
        Project subproject1 = Project.builder().visibility(ProjectVisibility.PUBLIC).build();
        Project subproject2 = Project.builder().visibility(ProjectVisibility.PUBLIC).build();
        ProjectVisibility parentVisibility = ProjectVisibility.PUBLIC;

        List<Project> subProjects = List.of(subproject1, subproject2);

        projectValidator.applyPrivateVisibilityIfParentIsPrivate(subProjects, parentVisibility);

        assertNotSame(subproject1.getVisibility(), ProjectVisibility.PRIVATE);
        assertNotSame(subproject2.getVisibility(), ProjectVisibility.PRIVATE);

    }

    @Test
    public void testApplyPrivateVisibilityIfParentIsPrivate() {
        Project subproject1 = Project.builder().visibility(ProjectVisibility.PUBLIC).build();
        Project subproject2 = Project.builder().visibility(ProjectVisibility.PUBLIC).build();
        ProjectVisibility parentVisibility = ProjectVisibility.PRIVATE;

        List<Project> subProjects = List.of(subproject1, subproject2);

        projectValidator.applyPrivateVisibilityIfParentIsPrivate(subProjects, parentVisibility);

        assertEquals(subproject1.getVisibility(), ProjectVisibility.PRIVATE);
        assertEquals(subproject2.getVisibility(), ProjectVisibility.PRIVATE);
    }

}
