package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.project.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private SubProjectMapper subProjectMapper;

    @Test
    void createSubProject() {
        CreateSubProjectDto subProjectDto = new CreateSubProjectDto();
        Project parentProject = Project.builder()
                .id(1L)
                .children(new ArrayList<>())
                .visibility(ProjectVisibility.PUBLIC).build();
        Project childProject = Project.builder().visibility(ProjectVisibility.PUBLIC).build();
        parentProject.getChildren().add(childProject);

        when(projectRepository.getProjectById(any())).thenReturn(parentProject);
        when(subProjectMapper.toEntity(any())).thenReturn(childProject);

        projectService.createSubProject(1L, subProjectDto);
        assertEquals(childProject.getParentProject(), parentProject);
        verify(projectRepository, times(1)).save(childProject);
        verify(projectRepository, times(1)).save(parentProject);
    }

    @Test
    void testCreateSubProjectDifferentVisibility() {
        CreateSubProjectDto subProjectDto = new CreateSubProjectDto();
        Project parentProject = Project.builder()
                .id(1L)
                .children(new ArrayList<>())
                .visibility(ProjectVisibility.PUBLIC).build();
        Project childProject = Project.builder().id(2L).visibility(ProjectVisibility.PRIVATE).build();
        parentProject.getChildren().add(childProject);

        when(projectRepository.getProjectById(any())).thenReturn(parentProject);
        when(subProjectMapper.toEntity(any())).thenReturn(childProject);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectService.createSubProject(1L, subProjectDto));
        assertTrue(exception.getMessage().contains("Sub project can't be private in public project"));
    }
}

























