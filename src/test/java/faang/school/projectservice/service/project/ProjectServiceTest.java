package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.AlreadyExistsException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.project.ProjectNameFilter;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    private static final String PROJECT = "Project";

    @Mock
    private ProjectJpaRepository projectRepository;

    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    private final List<Filter<Project,ProjectFilterDto>> projectFilters = new ArrayList<>();

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    public void setUp() {
        projectFilters.add(new ProjectNameFilter());
        projectFilters.add(new ProjectStatusFilter());

        projectService = new ProjectService(projectMapper, projectFilters, projectRepository);
    }

    @Test
    public void findByIdTest() {
        Project project = Project.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        ProjectDto expectedProjectDto = ProjectDto.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        ProjectDto result = projectService.findById(1L);

        verify(projectRepository).findById(1L);
        assertEquals(expectedProjectDto.getId(), result.getId());
        assertEquals(expectedProjectDto.getName(), result.getName());
    }

    @Test
    public void findByIdNotFoundTest() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(
                EntityNotFoundException.class,
                () -> projectService.findById(1L)
        );
    }

    @Test
    public void findAllProjectsTest() {
        Project project1 = Project.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        Project project2 = Project.builder()
                .id(2L)
                .name("Project2")
                .ownerId(2L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .description("Description2")
                .build();

        ProjectFilterDto filters = new ProjectFilterDto("Project1", ProjectStatus.CREATED);
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));

        List<ProjectDto> result = projectService.findAllProjects(filters, 1L);

        assertEquals(1, result.size());
        assertEquals(project1.getId(), result.get(0).getId());
    }

    @Test
    public void findAllProjectsNoFilterTest() {
        Project project1 = Project.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        Project project2 = Project.builder()
                .id(2L)
                .name("Project2")
                .ownerId(2L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .description("Description2")
                .build();

        ProjectFilterDto filters = new ProjectFilterDto(null, null);
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));

        List<ProjectDto> result = projectService.findAllProjects(filters, 1L);

        assertEquals(1, result.size());
        assertEquals(project1.getId(), result.get(0).getId());
    }

    @Test
    public void findAllProjectsNoMatchingFilterTest() {
        Project project1 = Project.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        Project project2 = Project.builder()
                .id(2L)
                .name("Project2")
                .ownerId(2L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .description("Description2")
                .build();

        ProjectFilterDto filters = new ProjectFilterDto("NonExistingProjectName", ProjectStatus.COMPLETED);
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));

        List<ProjectDto> result = projectService.findAllProjects(filters, 1L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void createProjectTest() {
        ProjectDto projectDto = ProjectDto.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        Project project = Project.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        when(projectRepository.existsByOwnerIdAndName(1L, "Project1")).thenReturn(false);
        when(projectRepository.save(project)).thenReturn(project);

        ProjectDto result = projectService.createProject(projectDto, 1L);

        verify(projectRepository).save(project);
        assertEquals(projectDto.getId(), result.getId());
        assertEquals(projectDto.getName(), result.getName());
    }

    @Test
    public void createProjectAlreadyExistsTest() {
        ProjectDto projectDto = ProjectDto.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        when(projectRepository.existsByOwnerIdAndName(1L, "Project1")).thenReturn(true);
        assertThrows(
                AlreadyExistsException.class,
                () -> projectService.createProject(projectDto, 1L)
        );
    }

    @Test
    public void updateProjectTest() {
        ProjectDto projectDto = ProjectDto.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        Project project = Project.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        ProjectDto result = projectService.updateProject(projectDto);

        verify(projectRepository).save(project);
        assertEquals(projectDto.getId(), result.getId());
        assertEquals(projectDto.getName(), result.getName());
    }

    @Test
    public void updateProjectNotFoundTest() {
        ProjectDto projectDto = ProjectDto.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(
                EntityNotFoundException.class,
                () -> projectService.updateProject(projectDto)
        );
    }

    @Test
    void isProjectExistsTrueTest() {
        when(projectRepository.existsById(1L)).thenReturn(true);

        boolean existsById = projectService.isProjectExists(1L);

        assertTrue(existsById);
        verify(projectRepository, times(1)).existsById(1L);
    }

    @Test
    void isProjectExistsFalseTest() {
        when(projectRepository.existsById(1L)).thenReturn(false);

        boolean existsById = projectService.isProjectExists(1L);

        assertFalse(existsById);
        verify(projectRepository, times(1)).existsById(1L);
    }

    @Test
    void getProjectByIdExistingProjectTest() {
        long projectId = 1L;
        Project project = Project.builder().id(projectId).build();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Project resultProject = projectService.getProjectById(projectId);

        assertEquals(project, resultProject);
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void getProjectByIdNotExistingProjectTest() {
        long projectId = 1L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> projectService.getProjectById(projectId));
        assertEquals("Entity %s with ID %s not found".formatted(PROJECT, projectId), exception.getMessage());
        verify(projectRepository, times(1)).findById(projectId);
    }
}