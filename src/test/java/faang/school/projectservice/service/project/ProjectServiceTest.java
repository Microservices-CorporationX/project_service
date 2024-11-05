package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.AlreadyExistsException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.project.ProjectFilter;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectJpaRepository projectRepository;

    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    private final List<ProjectFilter> projectFilters = new ArrayList<>();

    @InjectMocks
    private ProjectService projectService;

    private Project project1;
    private Project project2;
    private ProjectDto projectDto1;
    private ProjectDto projectDto2;

    @BeforeEach
    public void setUp() {
        project1 = Project.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        project2 = Project.builder()
                .id(2L)
                .name("Project2")
                .ownerId(2L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .description("Description2")
                .build();

        projectDto1 = ProjectDto.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        projectDto2 = ProjectDto.builder()
                .id(2L)
                .name("Project2")
                .ownerId(2L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .description("Description2")
                .build();

        projectFilters.add(new ProjectNameFilter());
        projectFilters.add(new ProjectStatusFilter());

        projectService = new ProjectService(projectMapper, projectFilters, projectRepository);
    }

    @Test
    public void findByIdTest() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));

        ProjectDto result = projectService.findById(1L);

        verify(projectRepository).findById(1L);
        assertEquals(projectDto1.getId(), result.getId());
        assertEquals(projectDto1.getName(), result.getName());
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
        List<Project> projects = List.of(project1, project2);
        when(projectRepository.findAll()).thenReturn(projects);
        ProjectFilterDto filters = new ProjectFilterDto("Project1", ProjectStatus.CREATED);

        List<ProjectDto> result = projectService.findAllProjects(filters, 1L);

        assertEquals(1, result.size());
        assertEquals(projectDto1.getId(), result.get(0).getId());
    }

    @Test
    public void findAllProjectsNoFilterTest() {
        List<Project> projects = List.of(project1, project2);
        when(projectRepository.findAll()).thenReturn(projects);
        ProjectFilterDto filters = new ProjectFilterDto(null, null);

        List<ProjectDto> result = projectService.findAllProjects(filters, 1L);

        assertEquals(1, result.size());
        assertEquals(projectDto1.getId(), result.get(0).getId());
    }

    @Test
    public void findAllProjectsNoMatchingFilterTest() {
        List<Project> projects = List.of(project1, project2);
        when(projectRepository.findAll()).thenReturn(projects);
        ProjectFilterDto filters = new ProjectFilterDto("NonExistingProjectName", ProjectStatus.COMPLETED);

        List<ProjectDto> result = projectService.findAllProjects(filters, 1L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void createProjectTest() {
        when(projectRepository.existsByOwnerIdAndName(1L, "Project1")).thenReturn(false);
        when(projectRepository.save(project1)).thenReturn(project1);

        ProjectDto result = projectService.createProject(projectDto1, 1L);

        verify(projectRepository).save(project1);
        assertEquals(projectDto1.getId(), result.getId());
        assertEquals(projectDto1.getName(), result.getName());
    }

    @Test
    public void createProjectAlreadyExistsTest() {
        when(projectRepository.existsByOwnerIdAndName(1L, "Project1")).thenReturn(true);
        assertThrows(
                AlreadyExistsException.class,
                () -> projectService.createProject(projectDto1, 1L)
        );
    }

    @Test
    public void updateProjectTest() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        when(projectRepository.save(project1)).thenReturn(project1);

        ProjectDto result = projectService.updateProject(projectDto1);

        verify(projectRepository).save(project1);
        assertEquals(projectDto1.getId(), result.getId());
        assertEquals(projectDto1.getName(), result.getName());
    }

    @Test
    public void updateProjectNotFoundTest() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(
                EntityNotFoundException.class,
                () -> projectService.updateProject(projectDto1)
        );
    }
}
