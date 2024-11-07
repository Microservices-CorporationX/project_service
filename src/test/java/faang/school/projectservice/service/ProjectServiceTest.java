package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectValidator projectValidator;

    @Spy
    private ProjectMapperImpl projectMapper;

    @InjectMocks
    private ProjectService projectService;

    @Captor
    private ArgumentCaptor<Project> captor;

    private ProjectDto projectDto;
    private Project project;
    private Long id;

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
                .status(null)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        id = project.getId();
    }

    @Test
    void testCreateProjectSuccessful() {
        doNothing().when(projectValidator).validateUniqueProject(projectDto);
        when(projectRepository.save(project)).thenReturn(project);
        project.setStatus(ProjectStatus.CREATED);

        projectService.createProject(projectDto);

        verify(projectRepository, times(1)).save(captor.capture());
        Project result = captor.getValue();
        assertEquals(result, project);
    }

    @Test
    void testUpdateProjectDescriptionSuccessful() {
        projectDto.setDescription("Another test project description.");
        doNothing().when(projectValidator).validateProjectExists(projectDto);
        doNothing().when(projectValidator).validateProjectDescriptionUpdatable(projectDto);
        when(projectRepository.getProjectById(id)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);

        ProjectDto result = projectService.updateProjectDescription(projectDto);

        verify(projectRepository).save(project);
        assertEquals(result.getDescription(), projectDto.getDescription());
    }

    @Test
    void testUpdateProjectStatusSuccessful() {
        projectDto.setStatus(ProjectStatus.COMPLETED);
        doNothing().when(projectValidator).validateProjectExists(projectDto);
        doNothing().when(projectValidator).validateProjectStatusUpdatable(projectDto);
        when(projectRepository.getProjectById(id)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);

        ProjectDto result = projectService.updateProjectStatus(projectDto);

        verify(projectRepository).save(project);
        assertEquals(result.getStatus(), projectDto.getStatus());
    }

    @Test
    void testUpdateProjectVisibilitySuccessful() {
        projectDto.setVisibility(ProjectVisibility.PRIVATE);
        doNothing().when(projectValidator).validateProjectExists(projectDto);
        doNothing().when(projectValidator).validateProjectVisibilityUpdatable(projectDto);
        when(projectRepository.getProjectById(id)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);

        ProjectDto result = projectService.updateProjectVisibility(projectDto);

        verify(projectRepository).save(project);
        assertEquals(result.getVisibility(), projectDto.getVisibility());
    }
}