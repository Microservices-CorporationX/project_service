package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.project.ProjectNameFilter;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.mapper.project.CreateSubProjectMapper;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.project.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTests {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private CreateSubProjectMapper createSubProjectMapper;
    private List<ProjectFilter> projectFilters;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectValidator projectValidator;

    @BeforeEach
    public void setUp() {
        ProjectFilter nameFilter = mock(ProjectNameFilter.class);
        ProjectFilter statusFilter = mock(ProjectStatusFilter.class);
        projectFilters = new ArrayList<>(List.of(nameFilter, statusFilter));
        projectService = new ProjectService(projectMapper, createSubProjectMapper, projectRepository, projectFilters, projectValidator);
    }

    @Test
    public void testCreateProject() {
        Project project = new Project();
        ProjectDto projectDto = new ProjectDto();
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        when(projectMapper.toDto(any())).thenReturn(projectDto);
        ProjectDto result = projectService.createProject(projectDto);
        assertEquals(projectDto, result);
        verify(projectValidator, times(1)).validateUniqueProject(projectDto);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void testCreateSubProject() {
        long parentId = 1L;
        CreateSubProjectDto createSubProjectDto = CreateSubProjectDto.builder().parentId(parentId).build();
        Project parent = new Project();
        Project project = new Project();
        List<Project> children = new ArrayList<>();
        parent.setId(parentId);
        parent.setChildren(children);
        when(createSubProjectMapper.toEntity(createSubProjectDto)).thenReturn(project);
        when(projectRepository.getProjectById(parentId)).thenReturn(parent);
        projectService.createSubProject(createSubProjectDto);
        verify(projectValidator, times(1)).validateUniqueProject(createSubProjectDto);
        assertEquals(parent, project.getParentProject());
        assertTrue(parent.getChildren().contains(project));
        verify(projectRepository, times(1)).save(project);
        verify(projectRepository, times(1)).save(parent);
    }

    @Test
    public void testUpdateVisibilityToPrivate() {
        Project child1 = Project.builder()
                .id(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project child2 = Project.builder()
                .id(2L)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        List<Project> children = new ArrayList<>(List.of(child1, child2));
        Project parent = Project.builder()
                .id(3L)
                .visibility(ProjectVisibility.PUBLIC)
                .children(children)
                .build();
        projectService.updateVisibility(parent, ProjectVisibility.PRIVATE, parent.getChildren());
        assertEquals(ProjectVisibility.PRIVATE, parent.getVisibility());
        verify(projectRepository, times(1)).save(child1);
        verify(projectRepository, times(1)).save(child2);
        assertEquals(ProjectVisibility.PRIVATE, child1.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, child2.getVisibility());
    }

    @Test
    public void testUpdateVisibilityToPublic() {
        Project child1 = Project.builder()
                .id(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project child2 = Project.builder()
                .id(2L)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        List<Project> children = new ArrayList<>(List.of(child1, child2));
        Project parent = Project.builder()
                .id(3L)
                .visibility(ProjectVisibility.PRIVATE)
                .children(children)
                .build();
        projectService.updateVisibility(parent, ProjectVisibility.PUBLIC, parent.getChildren());
        assertEquals(ProjectVisibility.PUBLIC, parent.getVisibility());
        assertEquals(ProjectVisibility.PUBLIC, child1.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, child2.getVisibility());
    }

    @Test
    public void testUpdateStatusToCompleted() {
        Project project = Project.builder()
                .id(1L)
                .moments(new ArrayList<>())
                .children(new ArrayList<>())
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        when(projectValidator.validateAllChildProjectsCompleted(project)).thenReturn(true);
        projectService.updateStatus(project, ProjectStatus.COMPLETED, project.getChildren());
        assertEquals(ProjectStatus.COMPLETED, project.getStatus());
        Moment completed = new Moment();
        completed.setName(String.format("Project with id = %s has been completed", project.getId()));
        assertTrue(project.getMoments().contains(completed));
    }

    @Test
    public void testUpdateStatusToInProgress() {
        Project child1 = Project.builder()
                .id(2L)
                .status(ProjectStatus.ON_HOLD)
                .children(new ArrayList<>())
                .build();
        Project child2 = Project.builder()
                .id(3L)
                .status(ProjectStatus.CREATED)
                .children(new ArrayList<>())
                .build();
        List<Project> children = new ArrayList<>(List.of(child1, child2));
        Project parent = Project.builder()
                .id(1L)
                .moments(new ArrayList<>())
                .status(ProjectStatus.ON_HOLD)
                .children(children)
                .build();
        projectService.updateStatus(parent, ProjectStatus.IN_PROGRESS, parent.getChildren());
        assertEquals(ProjectStatus.IN_PROGRESS, parent.getStatus());
        assertEquals(ProjectStatus.IN_PROGRESS, child1.getStatus());
        assertEquals(ProjectStatus.IN_PROGRESS, child2.getStatus());
    }

    @Test
    public void testUpdate() {
        long projectId = 1L;
        Project project = Project.builder()
                .id(projectId)
                .children(new ArrayList<>())
                .moments(new ArrayList<>())
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.ON_HOLD).build();
        CreateSubProjectDto dto = CreateSubProjectDto.builder()
                .id(projectId)
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(projectValidator.needToUpdateVisibility(project, dto)).thenReturn(true);
        when(projectValidator.needToUpdateStatus(project, dto)).thenReturn(true);
        projectService.update(dto);

        verify(projectRepository, times(1)).save(project);
        assertEquals(ProjectVisibility.PUBLIC, project.getVisibility());
        assertEquals(ProjectStatus.IN_PROGRESS, project.getStatus());
    }

    @Test
    public void testGetProjectsByFilters() {
        long projectId = 1L;
        Project child1 = Project.builder()
                .id(2L)
                .name("First")
                .children(List.of())
                .status(ProjectStatus.IN_PROGRESS)
                .moments(List.of(new Moment()))
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project child2 = Project.builder()
                .id(3L)
                .name("Second")
                .children(List.of())
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PRIVATE)
                .moments(List.of(new Moment()))
                .build();
        Project parent = Project.builder()
                .id(projectId)
                .name("Parent")
                .parentProject(null)
                .children(new ArrayList<>(List.of(child1, child2)))
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .namePattern("First")
                .statusPattern(ProjectStatus.IN_PROGRESS)
                .build();

        CreateSubProjectDto child1Dto = CreateSubProjectDto.builder()
                .id(2L)
                .name("First")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        when(projectRepository.getProjectById(projectId)).thenReturn(parent);

        when(projectFilters.get(0).isApplicable(filterDto)).thenReturn(true);
        when(projectFilters.get(1).isApplicable(filterDto)).thenReturn(true);
        when(projectFilters.get(0).apply(any(), any())).thenReturn(Stream.of(child1));
        when(projectFilters.get(1).apply(any(), any())).thenReturn(Stream.of(child1));

        when(createSubProjectMapper.toDto(child1)).thenReturn(child1Dto);

        List<CreateSubProjectDto> expected = List.of(child1Dto);
        List<CreateSubProjectDto> result = projectService.getProjectsByFilters(projectId, filterDto);

        assertEquals(expected, result);
    }

    @Test
    public void testGetProjectsByFiltersPrivate() {
        long projectId = 1L;
        Project child1 = Project.builder()
                .id(2L)
                .name("First")
                .children(List.of())
                .status(ProjectStatus.IN_PROGRESS)
                .moments(List.of(new Moment()))
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        Project child2 = Project.builder()
                .id(3L)
                .name("Second")
                .children(List.of())
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PRIVATE)
                .moments(List.of(new Moment()))
                .build();
        Project parent = Project.builder()
                .id(projectId)
                .name("Parent")
                .parentProject(null)
                .children(new ArrayList<>(List.of(child1, child2)))
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .namePattern("First")
                .statusPattern(ProjectStatus.IN_PROGRESS)
                .build();

        when(projectRepository.getProjectById(projectId)).thenReturn(parent);
        when(projectFilters.get(0).isApplicable(filterDto)).thenReturn(true);
        when(projectFilters.get(1).isApplicable(filterDto)).thenReturn(true);
        when(projectFilters.get(0).apply(any(), any())).thenReturn(Stream.of(child1));
        when(projectFilters.get(1).apply(any(), any())).thenReturn(Stream.of(child1));

        List<CreateSubProjectDto> expected = List.of();
        List<CreateSubProjectDto> result = projectService.getProjectsByFilters(projectId, filterDto);

        assertEquals(expected, result);
    }

}
