package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubProjectControllerTest {
    @InjectMocks
    private SubProjectController controller;

    @Mock
    private ProjectService projectService;

    @Test
    public void testCreateSubProject(){
        CreateSubProjectDto subProjectDto = CreateSubProjectDto.builder()
                .name("Test").build();
        controller.createSubProject(1L,subProjectDto);
        verify(projectService,times(1)).createSubProject(1L,subProjectDto);
    }

    @Test
    public void testGetProjectsByFilters(){
        FilterProjectDto filterDto = FilterProjectDto.builder().build();
        projectService.getProjectsByFilter(1L, filterDto);
        verify(projectService,times(1)).getProjectsByFilter(1L, filterDto);
    }
}