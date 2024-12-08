package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubProjectControllerTest {

    @InjectMocks
    private SubProjectController subProjectController;

    @Mock
    private ProjectService projectService;

    @Test
    void testCreateSubProject() {
        CreateSubProjectDto createSubProjectDto = new CreateSubProjectDto();
        subProjectController.createSubProject(createSubProjectDto);
        verify(projectService, times(1)).createSubProject(createSubProjectDto);
    }

    @Test
    void testUpdate() {
        CreateSubProjectDto createSubProjectDto = new CreateSubProjectDto();
        subProjectController.updateSubProject(createSubProjectDto);
        verify(projectService, times(1)).updateSubProject(createSubProjectDto);
    }

}
