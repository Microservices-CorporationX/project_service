package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    private Project parentProject;
    private Project subProject;
    private ProjectDto parentProjectDto;
    private ProjectDto subProjectDto;
    private CreateSubProjectDto createSubProjectDto;

    @BeforeEach
    void setUp() {
        parentProject = Project.builder()
                .id(1L)
                .name("Parent Project")
                .description("Parent Description")
                .ownerId(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .children(List.of())
                .build();

        subProject = Project.builder()
                .id(2L)
                .name("Sub Project")
                .description("Sub Description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .parentProject(parentProject)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .children(List.of())
                .build();

        parentProjectDto = ProjectDto.builder()
                .id(1L)
                .name("Parent Project")
                .description("Parent Description")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        subProjectDto = ProjectDto.builder()
                .id(2L)
                .name("Sub Project")
                .description("Sub Description")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        createSubProjectDto = CreateSubProjectDto.builder()
                .name("Sub Project")
                .description("Sub Description")
                .parentProjectId(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    @Test
    void createSubProject_ShouldSucceed_WhenParentExists() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(parentProject));
        when(projectMapper.toEntity(createSubProjectDto)).thenReturn(subProject);
        when(projectRepository.save(any(Project.class))).thenReturn(subProject);
        when(projectMapper.toDto(subProject)).thenReturn(subProjectDto);

        ProjectDto result = projectService.createSubProject(createSubProjectDto, 1L);

        assertNotNull(result);
        assertEquals("Sub Project", result.getName());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void createSubProject_ShouldFail_WhenParentNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.createSubProject(createSubProjectDto, 1L));

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void updateSubProject_ShouldSucceed_WhenValid() {
        when(projectRepository.findById(2L)).thenReturn(Optional.of(subProject));
        when(projectRepository.save(any(Project.class))).thenReturn(subProject);
        when(projectMapper.toDto(subProject)).thenReturn(subProjectDto);

        subProjectDto.setStatus(ProjectStatus.COMPLETED);
        ProjectDto result = projectService.updateSubProject(subProjectDto);

        assertNotNull(result);
        assertEquals(ProjectStatus.COMPLETED, result.getStatus());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void updateSubProject_ShouldFail_WhenOpenChildExists() {
        Project childSubProject = Project.builder()
                .id(3L)
                .name("Child Sub Project")
                .status(ProjectStatus.IN_PROGRESS)
                .parentProject(subProject)
                .children(List.of())
                .build();

        subProject.setChildren(List.of(childSubProject));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(subProject));

        subProjectDto.setStatus(ProjectStatus.COMPLETED);
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> projectService.updateSubProject(subProjectDto));

        assertEquals("Cannot close project while it has open subprojects", exception.getMessage());
    }

    @Test
    void getSubProjects_ShouldReturnFilteredList() {
        parentProject.setChildren(List.of(subProject));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(parentProject));
        when(projectMapper.toDto(subProject)).thenReturn(subProjectDto);

        List<ProjectDto> result = projectService.getSubProjects(1L, "Sub", ProjectStatus.CREATED, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sub Project", result.get(0).getName());
    }
}