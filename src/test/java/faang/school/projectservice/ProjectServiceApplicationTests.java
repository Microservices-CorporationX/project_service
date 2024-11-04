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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        projectDto.setStatus("CREATED");
        projectDto.setVisibility("PUBLIC");
        projectDto.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createProject_Success() {
        when(projectRepository.existsByOwnerUserIdAndName(100L, "Test Project")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.createProject("Test Project", "A sample project description", 100L);

        verify(projectRepository).existsByOwnerUserIdAndName(100L, "Test Project");
        verify(projectRepository).save(any(Project.class));
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Project");
        assertThat(result.getDescription()).isEqualTo("A sample project description");
        assertThat(result.getOwnerId()).isEqualTo(100L);
        assertThat(result.getStatus()).isEqualTo("CREATED");
    }

    @Test
    void createProject_ProjectWithSameNameExists() {
        when(projectRepository.existsByOwnerUserIdAndName(100L, "Test Project")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> projectService.createProject("Test Project", "A sample project description", 100L)
        );

        assertThat(exception.getMessage()).contains("Project with the same name already exists for this owner.");
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void getProjectById_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.getProjectById(1L);

        verify(projectRepository).findById(1L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Project");
    }

    @Test
    void getProjectById_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> projectService.getProjectById(1L)
        );

        assertThat(exception.getMessage()).contains("Project not found");
    }

    @Test
    void findProjects_PublicAndOwnedProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectMapper.toDto(any(Project.class))).thenReturn(projectDto);

        List<ProjectDto> result = projectService.findProjects("Test Project", ProjectStatus.CREATED, 100L);

        verify(projectRepository).findAll();
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getName()).isEqualTo("Test Project");
    }
}
