package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

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
                .description("Test Description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        projectDto = ProjectDto.builder()
                .id(1L)
                .name("Test Project")
                .description("Test Description")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    @Test
    void createProject_shouldSaveProject() {
        when(projectRepository.existsByOwnerIdAndName(1L, "Test Project")).thenReturn(false);
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.createProject(projectDto, 1L);

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void createProject_shouldThrowExceptionIfProjectExists() {
        when(projectRepository.existsByOwnerIdAndName(1L, "Test Project")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.createProject(projectDto, 1L));

        assertEquals("Project with the same name already exists for this owner", exception.getMessage());
    }

    @Test
    void updateProject_shouldUpdateExistingProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(any(Project.class))).thenReturn(projectDto);

        ProjectDto updatedDto = projectService.updateProject(projectDto);

        assertNotNull(updatedDto);
        assertEquals("Test Project", updatedDto.getName());
        verify(projectRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void updateProject_shouldThrowExceptionIfNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.updateProject(projectDto));

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void getProjects_shouldFilterProjectsByNameAndStatus() {
        Project anotherProject = Project.builder()
                .id(2L)
                .name("Another Project")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        when(projectRepository.findAll()).thenReturn(List.of(project, anotherProject));
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        List<ProjectDto> result = projectService.getProjects("Test Project", ProjectStatus.CREATED, 1L);

        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getName());
    }

    @Test
    void getProjectsByFilter_shouldReturnProjectsMatchingFilter() {
        Project privateProject = Project.builder()
                .id(2L)
                .name("Private Project")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .ownerId(2L)
                .build();

        when(projectRepository.findAll()).thenReturn(List.of(project, privateProject));
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        List<ProjectDto> result = projectService.getProjectsByFilter("Test Project", ProjectStatus.CREATED, 1L);

        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getName());
    }

    @Test
    void getAllProjects_shouldReturnAllProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        List<ProjectDto> result = projectService.getAllProjects();

        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getName());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void getAllProjects_shouldReturnEmptyListIfNoProjectsExist() {
        when(projectRepository.findAll()).thenReturn(List.of());

        List<ProjectDto> result = projectService.getAllProjects();

        assertTrue(result.isEmpty());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void getProjectById_shouldReturnProjectIfVisible() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.getProjectById(1L, 1L);

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void getProjectById_shouldThrowExceptionIfNotVisible() {
        project.setVisibility(ProjectVisibility.PRIVATE);
        project.setOwnerId(2L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        IllegalAccessException exception = assertThrows(IllegalAccessException.class,
                () -> projectService.getProjectById(1L, 1L));

        assertEquals("You don't have access to this project", exception.getMessage());
    }

    @Test
    void getProjectById_shouldThrowExceptionIfNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.getProjectById(1L, 1L));

        assertEquals("Project not found", exception.getMessage());
    }
}