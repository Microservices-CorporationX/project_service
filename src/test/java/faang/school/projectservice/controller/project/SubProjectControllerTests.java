package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubProjectControllerTests {

    @InjectMocks
    private SubProjectController subProjectController;

    @Mock
    private ProjectService projectService;

    @Test
    void testCreateProject() {
        ProjectDto projectDto = new ProjectDto();
        subProjectController.createProject(projectDto);
        verify(projectService, times(1)).createProject(projectDto);
    }

    @Test
    void testCreateSubProject() {
        CreateSubProjectDto createSubProjectDto = new CreateSubProjectDto();
        subProjectController.createSubProject(1L, createSubProjectDto);
        verify(projectService, times(1)).createSubProject(1L, createSubProjectDto);
    }

    @Test
    void testUpdate() {
        CreateSubProjectDto createSubProjectDto = new CreateSubProjectDto();
        subProjectController.update(1L, createSubProjectDto);
        verify(projectService, times(1)).update(1L, createSubProjectDto);
    }

    @Test
    void testGetProjectsByFilters(){
        ProjectFilterDto projectFilterDto = new ProjectFilterDto();
        subProjectController.getProjectsByFilters(projectFilterDto, 1L);
        verify(projectService, times(1)).getProjectsByFilters(1L, projectFilterDto);
    }

}
