package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.update.ProjectUpdate;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private ProjectValidator projectValidator;

    @Spy
    private ProjectMapperImpl projectMapper;

    private List<ProjectUpdate> projectUpdates;

    private List<ProjectFilter> projectFilters;

    @Test
    public void testGetProjectByIdSuccessful() {
        Project project = new Project();
        project.setId(1L);

        when(projectRepository.getProjectById(project.getId())).thenReturn(project);
        ProjectDto result = projectService.getProjectById(project.getId());

        assertEquals(project.getId(), result.getId());
    }

    @Test
    public void testGetProjectByIdFailed() {
        when(projectRepository.getProjectById(0L)).thenThrow(new EntityNotFoundException());
        assertThrows(EntityNotFoundException.class,
                () -> projectService.getProjectById(0L));
    }

    @Test
    public void testGetAllProjectsSuccessful() {
        ProjectFilterDto projectFilterDto = new ProjectFilterDto();
        projectFilterDto.setName("name");
        projectFilterDto.setStatus("ON_HOLD");

        Project privateProject = new Project();
        Project publicProject = new Project();
        privateProject.setVisibility(ProjectVisibility.PRIVATE);
        publicProject.setVisibility(ProjectVisibility.PUBLIC);

        when(projectRepository.findAll()).thenReturn(List.of(
                privateProject,
                publicProject
        ));
    }
}
