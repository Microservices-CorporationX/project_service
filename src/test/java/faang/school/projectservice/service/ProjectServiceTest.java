package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.mapper.project.UpdateProjectMapperImpl;
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
import static org.mockito.Mockito.mock;
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

    @Spy
    private UpdateProjectMapperImpl updateProjectMapper;

    @InjectMocks
    private ProjectService projectService;

    @Captor
    private ArgumentCaptor<Project> captor;

    private Project project;
    private ProjectDto projectDto;
    private UpdateProjectDto updateProjectDto;
    private UpdateProjectDto emptyUpdateProjectDto;
    private Project mockProject;

    private Long id;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(null)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        projectDto = ProjectDto.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        updateProjectDto = UpdateProjectDto.builder()
                .name("Test project")
                .description("Updated test project description")
                .ownerId(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        emptyUpdateProjectDto = UpdateProjectDto.builder()
                .name("Test project")
                .description(null)
                .ownerId(1L)
                .status(null)
                .visibility(null)
                .build();

        id = project.getId();
        mockProject = mock(Project.class);
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
    void testUpdateProjectShouldNotUpdateIfValuesNull() {
        when(projectRepository.getProjectById(id)).thenReturn(mockProject);
        when(projectRepository.save(mockProject)).thenReturn(mockProject);

        projectService.updateProject(emptyUpdateProjectDto);

        verify(mockProject, times(0)).setDescription(updateProjectDto.getDescription());
        verify(mockProject, times(0)).setStatus(updateProjectDto.getStatus());
        verify(mockProject, times(0)).setVisibility(updateProjectDto.getVisibility());
        verify(projectRepository, times(1)).save(mockProject);
    }

    @Test
    void testUpdateProjectSuccessful() {
        when(projectRepository.getProjectById(id)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);

        UpdateProjectDto result = projectService.updateProject(updateProjectDto);

        verify(projectRepository, times(1)).save(project);
        assertEquals(result.getDescription(), updateProjectDto.getDescription());
        assertEquals(result.getStatus(), updateProjectDto.getStatus());
        assertEquals(result.getVisibility(), updateProjectDto.getVisibility());
    }
}