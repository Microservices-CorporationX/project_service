package faang.school.projectservice.validator.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {

    @InjectMocks
    private ProjectValidator projectValidator;

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserContext userContext;

    @Test
    void testValidateName(){
        Long id = 1L;
        String name = "name";
        when(projectRepository.existsByOwnerUserIdAndName(id, name)).thenReturn(true);
        when(userContext.getUserId()).thenReturn(1L);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectValidator.validateName(name, id));
        assertEquals("Can not create new project with this project name, " +
                "this name is already used for another project of this user", exception.getMessage());
    }



    @Test
    public void testValidateUniqueProject(){
        ProjectDto projectDto = ProjectDto.builder().id(1L).build();
        when(projectRepository.existsById(Mockito.anyLong())).thenReturn(true);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectValidator.validateUniqueProject(projectDto));
        assertEquals("Project with id 1 already exists", exception.getMessage());
    }

    @Test
    public void testValidateUniqueSubProject(){
        CreateSubProjectDto createSubProjectDto = CreateSubProjectDto.builder().id(1L).build();
        when(projectRepository.existsById(Mockito.anyLong())).thenReturn(true);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectValidator.validateUniqueProject(createSubProjectDto));
        assertEquals("Project with id 1 already exists", exception.getMessage());
    }

    @Test
    public void testValidateIsPublic(){
        Project project = Project.builder().id(1L).visibility(ProjectVisibility.PRIVATE).build();
        Project parent = Project.builder().id(2L).visibility(ProjectVisibility.PUBLIC).build();
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectValidator.validateIsPublic(parent, project));
        assertEquals("Cannot create private subproject for public project", exception.getMessage());
    }

    @Test
    public void testValidateProjectAlreadyCompleted(){
        Project project = Project.builder().id(1L).status(ProjectStatus.COMPLETED).build();
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectAlreadyCompleted(project));
        assertEquals("Project with id " + project.getId() + " already completed", exception.getMessage());
    }

    @Test
    public void testValidateAllChildProjectsCompletedPositive(){
        Project child1 = Project.builder().id(1L).status(ProjectStatus.COMPLETED).build();
        Project child2 = Project.builder().id(2L).status(ProjectStatus.COMPLETED).build();
        Project parent = Project.builder().id(3L).children(List.of(child1, child2)).build();
        assertTrue(projectValidator.validateAllChildProjectsCompleted(parent));
    }

    @Test
    public void testValidateAllChildProjectsCompletedNegative(){
        Project child1 = Project.builder().id(1L).status(ProjectStatus.IN_PROGRESS).build();
        Project child2 = Project.builder().id(2L).status(ProjectStatus.COMPLETED).build();
        Project parent = Project.builder().id(3L).children(List.of(child1, child2)).build();
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectValidator.validateAllChildProjectsCompleted(parent));
        assertEquals("Project id: " + parent.getId() + " has unfinished subprojects", exception.getMessage());
    }

    @Test
    public void testValidateProjectExists(){
        CreateSubProjectDto dto = CreateSubProjectDto.builder().id(1L).build();
        when(projectRepository.existsById(Mockito.anyLong())).thenReturn(false);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectExists(dto));
        assertEquals("Project with id " + dto.getId() + " does not exist", exception.getMessage());
    }

    @Test
    public void testNeedToUpdateVisibility() {
        CreateSubProjectDto dto = CreateSubProjectDto.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        Project project = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        assertTrue(projectValidator.needToUpdateVisibility(project, dto));
        project.setVisibility(ProjectVisibility.PRIVATE);
        assertFalse(projectValidator.needToUpdateVisibility(project, dto));
    }

    @Test
    public void testNeedToUpdateStatus() {
        CreateSubProjectDto dto = CreateSubProjectDto.builder()
                .status(ProjectStatus.ON_HOLD)
                .build();
        Project project = Project.builder()
                .status(ProjectStatus.CREATED)
                .build();
        assertTrue(projectValidator.needToUpdateStatus(project, dto));
        project.setStatus(ProjectStatus.ON_HOLD);
        assertFalse(projectValidator.needToUpdateStatus(project, dto));
    }
}
