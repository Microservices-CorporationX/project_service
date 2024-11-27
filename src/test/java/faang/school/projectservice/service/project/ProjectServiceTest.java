package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filters.project.ProjectFilter;
import faang.school.projectservice.mapper.project.CreateSubProjectMapper;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.validator.project.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    private static final String NOT_OWNER_UPDATING_MESSAGE = "The user is not the owner of this project";

    @InjectMocks
    private ProjectService projectService;

    @Spy
    private ProjectMapperImpl projectMapper;
    @Mock
    private CreateSubProjectMapper createSubProjectMapper;
    @Spy
    private faang.school.projectservice.filters.project.NameProjectFilter nameProjectFilter;
    @Spy
    private faang.school.projectservice.filters.project.StatusProjectFilter statusProjectFilter;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private UserContext userContext;
    @Mock
    private MomentService momentService;

    @BeforeEach
    void setUp() {
        List<ProjectFilter> filters = List.of(nameProjectFilter, statusProjectFilter);
        projectService = new ProjectService(userContext, projectMapper, createSubProjectMapper, projectRepository, filters, projectValidator, momentService);
    }

    @Test
    void testCreateCreated() {
        ProjectDto projectDto = ProjectDto.builder()
                .name("Project")
                .description("Description")
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        long userId = 1L;
        long generatedId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        Project expectedCreatedProject = projectMapper.toEntity(projectDto);
        expectedCreatedProject.setChildren(new ArrayList<>());
        expectedCreatedProject.setStatus(ProjectStatus.CREATED);
        expectedCreatedProject.setId(generatedId);
        when(projectRepository.save(any(Project.class))).thenReturn(expectedCreatedProject);

        ProjectDto createdProjectDto = projectService.create(projectDto);

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectCaptor.capture());
        Project capturedProject = projectCaptor.getValue();
        assertNotNull(capturedProject);
        assertEquals(ProjectStatus.CREATED, capturedProject.getStatus());
        assertNotNull(capturedProject.getCreatedAt());
        assertNotNull(capturedProject.getUpdatedAt());
        assertEquals(expectedCreatedProject, projectMapper.toEntity(createdProjectDto));
    }

    @Test
    void testUpdateStatusWithUserNotOwner() {
        long userId = 1L;
        long projectId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.getProjectById(projectId))
                .thenReturn(Project.builder().ownerId(2L).build());
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectService.updateStatus(ProjectStatus.CANCELLED, projectId));
        assertEquals(NOT_OWNER_UPDATING_MESSAGE, exception.getMessage());
    }

    @Test
    void testUpdateStatusUpdated() {
        long userId = 1L;
        long projectId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        Project project = Project.builder().ownerId(userId).children(new ArrayList<>()).status(ProjectStatus.CREATED).build();
        when(projectRepository.getProjectById(projectId))
                .thenReturn(project);
        ProjectDto expectedDto = projectMapper.toDto(project);
        expectedDto.setStatus(ProjectStatus.IN_PROGRESS);
        Project updatedProject = projectMapper.toEntity(expectedDto);
        updatedProject.setChildren(new ArrayList<>());
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        ProjectDto actualDto = projectService.updateStatus(ProjectStatus.IN_PROGRESS, projectId);

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectCaptor.capture());
        assertEquals(updatedProject.getStatus(), projectCaptor.getValue().getStatus());
        assertNotNull(projectCaptor.getValue().getUpdatedAt());
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testUpdateDescriptionWithUserNotOwner() {
        long userId = 1L;
        long projectId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.getProjectById(projectId))
                .thenReturn(Project.builder().ownerId(2L).build());
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectService.updateDescription("New description", projectId));
        assertEquals(NOT_OWNER_UPDATING_MESSAGE, exception.getMessage());
    }

    @Test
    void testUpdateDescriptionUpdated() {
        long userId = 1L;
        long projectId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        Project project = Project.builder().ownerId(userId).children(new ArrayList<>()).description("Old").build();
        when(projectRepository.getProjectById(projectId))
                .thenReturn(project);
        ProjectDto expectedDto = projectMapper.toDto(project);
        expectedDto.setDescription("New");
        Project updatedProject = projectMapper.toEntity(expectedDto);
        updatedProject.setChildren(new ArrayList<>());
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        ProjectDto actualDto = projectService.updateDescription("New", projectId);

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectCaptor.capture());
        assertEquals(updatedProject.getDescription(), projectCaptor.getValue().getDescription());
        assertNotNull(projectCaptor.getValue().getUpdatedAt());
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testFindWithFiltersFoundPublic() {
        Project project1 = Project.builder()
                .visibility(ProjectVisibility.PUBLIC).id(1L).ownerId(2L).name("name").children(new ArrayList<>()).status(ProjectStatus.CREATED)
                .build();
        Project project2 = Project.builder()
                .visibility(ProjectVisibility.PUBLIC).id(2L).ownerId(2L).name("name").children(new ArrayList<>()).status(ProjectStatus.CANCELLED)
                .build();
        Project project3 = Project.builder()
                .visibility(ProjectVisibility.PUBLIC).id(3L).ownerId(1L).name("no").children(new ArrayList<>()).status(ProjectStatus.CREATED)
                .build();

        List<Project> projects = List.of(project1, project2, project3);
        List<Project> filteredProjects = List.of(project1);
        List<ProjectDto> expected = filteredProjects.stream().map(project -> projectMapper.toDto(project)).toList();
        when(projectRepository.findAll()).thenReturn(projects);
        ProjectFilterDto projectFilterDto = new ProjectFilterDto("name", ProjectStatus.CREATED);
        List<ProjectDto> actual = projectService.findWithFilters(projectFilterDto);
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void testFindWithFiltersFoundPrivate() {
        Project project1 = Project.builder()
                .visibility(ProjectVisibility.PRIVATE).id(1L).ownerId(1L).name("name").children(new ArrayList<>()).status(ProjectStatus.CREATED)
                .build();
        Project project2 = Project.builder()
                .visibility(ProjectVisibility.PRIVATE).id(2L).ownerId(2L).name("name").children(new ArrayList<>()).status(ProjectStatus.CREATED)
                .build();
        Team team = Team.builder().teamMembers(List.of(TeamMember.builder().userId(1L).build())).build();
        Project project3 = Project.builder()
                .visibility(ProjectVisibility.PRIVATE).id(3L).ownerId(2L).teams(List.of(team)).name("name")
                .status(ProjectStatus.CREATED)
                .children(new ArrayList<>())
                .build();

        List<Project> projects = List.of(project1, project2, project3);
        List<Project> filteredProjects = List.of(project1, project3);
        List<ProjectDto> expected = filteredProjects.stream().map(project -> projectMapper.toDto(project)).toList();
        when(projectRepository.findAll()).thenReturn(projects);
        when(userContext.getUserId()).thenReturn(1L);
        ProjectFilterDto projectFilterDto = new ProjectFilterDto("name", ProjectStatus.CREATED);
        List<ProjectDto> actual = projectService.findWithFilters(projectFilterDto);
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void testFindAll() {
        Project project1 = Project.builder()
                .visibility(ProjectVisibility.PRIVATE).id(1L).ownerId(1L).name("name").children(new ArrayList<>()).status(ProjectStatus.CREATED)
                .build();
        Project project2 = Project.builder()
                .visibility(ProjectVisibility.PRIVATE).id(2L).ownerId(2L).name("name").children(new ArrayList<>()).status(ProjectStatus.CREATED)
                .build();
        Team team = Team.builder().teamMembers(List.of(TeamMember.builder().userId(1L).build())).build();
        Project project3 = Project.builder()
                .visibility(ProjectVisibility.PRIVATE).id(3L).ownerId(2L).teams(List.of(team)).name("name")
                .status(ProjectStatus.CREATED)
                .children(new ArrayList<>())
                .build();

        List<Project> projects = List.of(project1, project2, project3);
        List<Project> allowedToSee = List.of(project1, project3);
        List<ProjectDto> expected = allowedToSee.stream().map(project -> projectMapper.toDto(project)).toList();
        when(projectRepository.findAll()).thenReturn(projects);
        when(userContext.getUserId()).thenReturn(1L);
        List<ProjectDto> actual = projectService.findAll();
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void testFindByIdWithPrivateProject() {
        when(projectRepository.getProjectById(1L))
                .thenReturn(Project.builder().ownerId(2L).visibility(ProjectVisibility.PRIVATE).build());
        when(userContext.getUserId()).thenReturn(1L);
        assertTrue(projectService.findById(1L).isEmpty());
    }

    @Test
    void testFindByFound() {
        Project expected = Project.builder().ownerId(2L).children(new ArrayList<>()).visibility(ProjectVisibility.PUBLIC).build();
        when(projectRepository.getProjectById(1L))
                .thenReturn(expected);
        Optional<ProjectDto> actual = projectService.findById(1L);
        assertTrue(actual.isPresent());
        assertEquals(projectMapper.toDto(expected), actual.get());
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
        projectService.updateSubProjectsVisibility(parent, ProjectVisibility.PRIVATE, parent.getChildren());
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
        projectService.updateSubProjectsVisibility(parent, ProjectVisibility.PUBLIC, parent.getChildren());
        assertEquals(ProjectVisibility.PUBLIC, parent.getVisibility());
        assertEquals(ProjectVisibility.PUBLIC, child1.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, child2.getVisibility());
    }

    @Test
    public void testUpdateStatusToCompleted() {
        Project project = Project.builder()
                .name("Test")
                .id(1L)
                .moments(new ArrayList<>())
                .children(new ArrayList<>())
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        when(projectValidator.validateAllChildProjectsCompleted(project)).thenReturn(true);
        projectService.updateSubProjectsStatus(project, ProjectStatus.COMPLETED, project.getChildren());
        assertEquals(ProjectStatus.COMPLETED, project.getStatus());
        MomentDto momentDto = MomentDto.builder()
                .name(project.getName() + " completed")
                .description("Project with id: " + project.getId() + " has been completed")
                .date(LocalDateTime.now())
                .projectIds(List.of(project.getId()))
                .build();
        verify(momentService, times(1)).createMoment(any());
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
        projectService.updateSubProjectsStatus(parent, ProjectStatus.IN_PROGRESS, parent.getChildren());
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
        projectService.updateSubProject(dto);

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

        List<CreateSubProjectDto> expected = List.of();
        List<CreateSubProjectDto> result = projectService.getProjectsByFilters(projectId, filterDto);

        assertEquals(expected, result);
    }

}
