package faang.school.projectservice;

import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceApplicationTests {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("A sample project description")
                .ownerId(100L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .build();

        projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setName("Test Project");
        projectDto.setDescription("A sample project description");
        projectDto.setOwnerId(100L);
        projectDto.setStatus(ProjectStatus.CREATED);
        projectDto.setVisibility(ProjectVisibility.PUBLIC);
        projectDto.setCreatedAt(LocalDateTime.now());


        lenient().when(projectMapper.toDto(any(Project.class))).thenReturn(projectDto);
    }

    @Test
    void createProject_Success() {
        when(projectRepository.existsByOwnerUserIdAndName(100L, "Test Project")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectDto result = projectService.createProject(projectDto);

        verify(projectRepository).existsByOwnerUserIdAndName(100L, "Test Project");
        verify(projectRepository).save(any(Project.class));

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Project");
        assertThat(result.getDescription()).isEqualTo("A sample project description");
        assertThat(result.getOwnerId()).isEqualTo(100L);
        assertThat(result.getStatus()).isEqualTo(ProjectStatus.CREATED);
    }

    @Test
    void createProject_ProjectWithSameNameExists() {
        when(projectRepository.existsByOwnerUserIdAndName(100L, "Test Project")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> projectService.createProject(projectDto)
        );

        assertThat(exception.getMessage()).contains("Project with the same name already exists for this owner.");
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void getProjectById_Success() {
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        ProjectDto result = projectService.getProjectById(1L);

        verify(projectRepository).getProjectById(1L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Project");
    }

    @Test
    void getProjectById_NotFound() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"))
                .when(projectRepository).getProjectById(1L);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> projectService.getProjectById(1L)
        );

        assertThat(exception.getMessage()).contains("Project not found");
    }

    @Test
    void findProjects_PublicAndOwnedProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<ProjectDto> result = projectService.findProjects("Test Project", ProjectStatus.CREATED, ProjectVisibility.PUBLIC, 100L);

        verify(projectRepository).findAll();
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getName()).isEqualTo("Test Project");
        assertThat(result).allMatch(dto -> "Test Project".equals(dto.getName()));
    }
}
