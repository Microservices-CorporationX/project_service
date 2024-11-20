package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.mapper.project.SubProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.update.ProjectUpdate;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private ProjectValidator projectValidator;

    @Spy
    private ProjectMapperImpl projectMapper;

    @Spy
    private SubProjectMapperImpl subProjectMapper;

    @Mock
    private ProjectUpdate projectUpdate;

    @Mock
    private ProjectFilter filter;

    private List<ProjectUpdate> projectUpdates;

    private List<ProjectFilter> projectFilters;
    private Project project = new Project();
    private CreateSubProjectDto createSubProjectDto = new CreateSubProjectDto();
    private ProjectDto projectDto = new ProjectDto();
    private Project firstSubProject = new Project();
    private ProjectFilterDto projectFilterDto = new ProjectFilterDto();

    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    @BeforeEach
    public void setUp() {
        List<ProjectFilter> projectFilters = List.of(filter, filter);
        List<ProjectUpdate> projectUpdates = List.of(projectUpdate, projectUpdate);

        projectService = new ProjectService(projectRepository, subProjectMapper, projectMapper, projectUpdates, projectFilters, projectValidator, userContext);
    }

    @Test
    void testCreateSubProjectWithPrivateVisibility() {
        project.setVisibility(ProjectVisibility.PUBLIC);
        createSubProjectDto.setVisibility(ProjectVisibility.PRIVATE);

        Mockito.when(projectRepository.getProjectById(9L)).thenReturn(project);
        assertThrows(IllegalArgumentException.class, () -> projectService.createSubProject(9L, createSubProjectDto));
    }

    @Test
    void testCreateSubProjectWithSameName() {
        Project subProject = new Project();
        subProject.setName("testName");
        project.setChildren(List.of(subProject));
        project.setName("anotherName");
        createSubProjectDto.setName("testName");

        Mockito.when(projectRepository.getProjectById(9L)).thenReturn(project);
        assertThrows(IllegalArgumentException.class, () -> projectService.createSubProject(9L, createSubProjectDto));
    }

    @Test
    void testSuccessfulSubProject() {
        project.setName("anotherName");
        createSubProjectDto.setName("testName");
        Mockito.when(projectRepository.getProjectById(9L)).thenReturn(project);
        ProjectDto projectDto = projectService.createSubProject(9L, createSubProjectDto);

        Mockito.verify(subProjectMapper).toProject(createSubProjectDto);
        Mockito.verify(projectRepository, Mockito.times(2)).save(projectCaptor.capture());
        Mockito.verify(projectMapper).toProjectDto(projectCaptor.getAllValues().get(0));

        assertEquals(projectCaptor.getAllValues().get(0).getName(), createSubProjectDto.getName());
        assertEquals(projectCaptor.getAllValues().get(1).getName(), project.getName());
        assertEquals(projectDto.getName(), createSubProjectDto.getName());
        assertEquals(project.getChildren().get(0).getName(), createSubProjectDto.getName());
    }

    @Test
    void testNullIdForUpdateSubProject() {
        projectDto.setId(null);
        assertThrows(IllegalArgumentException.class, () -> projectService.updateSubProject(projectDto));
    }

    @Test
    void testInvalidIdForUpdateSubProject() {
        projectDto.setId(-11L);
        assertThrows(IllegalArgumentException.class, () -> projectService.updateSubProject(projectDto));
    }

    @Test
    void testCancelledStatusForUpdateSubProject() {
        firstSubProject.setStatus(ProjectStatus.IN_PROGRESS);

        project.setStatus(ProjectStatus.CANCELLED);
        projectDto.setId(8L);
        projectDto.setChildrenIds(List.of(1L, 1L));

        Mockito.when(projectRepository.getProjectById(8L)).thenReturn(project);
        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(firstSubProject);

        assertThrows(IllegalStateException.class, () -> projectService.updateSubProject(projectDto));
    }

    @Test
    void testSuccessForUpdateSubProject() {
        Mockito.when(projectRepository.getProjectById(8L)).thenReturn(project);
        projectDto.setId(8L);
        projectDto.setName("Test");
        projectDto.setDescription("TestDescription");

        project.setName("Test");
        project.setDescription("TestDescription");

        projectService.updateSubProject(projectDto);
        Mockito.verify(projectRepository).save(projectCaptor.capture());
        Project resultProject = projectCaptor.getValue();

        assertEquals(resultProject.getName(), "Test");
        assertEquals(resultProject.getDescription(), "TestDescription");
        assertNotNull(resultProject.getUpdatedAt());
        assertNotNull(resultProject.getMoments());
        assertEquals(resultProject.getMoments().get(0).getName(), "allSubProjectCancelled");
    }

    @Test
    void testSuccessForUpdateSubProjectWithOutMoment() {
        firstSubProject.setStatus(ProjectStatus.IN_PROGRESS);

        project.setStatus(ProjectStatus.COMPLETED);
        project.setName("Test");
        project.setDescription("TestDescription");
        projectDto.setId(8L);
        projectDto.setChildrenIds(List.of(1L, 1L));

        Mockito.when(projectRepository.getProjectById(8L)).thenReturn(project);
        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(firstSubProject);

        projectService.updateSubProject(projectDto);
        Mockito.verify(projectRepository).save(projectCaptor.capture());
        Project resultProject = projectCaptor.getValue();
        assertEquals(resultProject.getName(), "Test");
        assertEquals(resultProject.getDescription(), "TestDescription");
        assertNotNull(resultProject.getUpdatedAt());
        assertNull(resultProject.getMoments());
    }

    @Test
    void testChildWithSameName() {
        Mockito.when(projectRepository.getProjectById(8L)).thenReturn(project);

        assertThrows(NullPointerException.class, () -> projectService.getSubProjects(8L, projectFilterDto));
    }

    @Test
    void testGetSubProjectsSuccess() {
        Project secondSubProject = new Project();
        Project thirdSubProject = new Project();
        project.setVisibility(ProjectVisibility.PUBLIC);

        firstSubProject.setName("Name");
        firstSubProject.setStatus(ProjectStatus.CREATED);
        firstSubProject.setVisibility(ProjectVisibility.PUBLIC);

        secondSubProject.setName("Name");
        secondSubProject.setStatus(ProjectStatus.CREATED);
        secondSubProject.setVisibility(ProjectVisibility.PRIVATE);

        thirdSubProject.setName("Not name");
        thirdSubProject.setStatus(ProjectStatus.IN_PROGRESS);
        thirdSubProject.setVisibility(ProjectVisibility.PUBLIC);

        projectFilterDto.setName("Name");
        projectFilterDto.setStatus(ProjectStatus.CREATED);

        project.setChildren(List.of(firstSubProject, secondSubProject, thirdSubProject));

        Mockito.when(projectRepository.getProjectById(8L)).thenReturn(project);
        List<ProjectDto> projectDtos = projectService.getSubProjects(8L, projectFilterDto);

        assertEquals(projectDtos.size(), 2);
        assertEquals(projectDtos.get(0).getName(), firstSubProject.getName());
        assertEquals(projectDtos.get(0).getStatus(), firstSubProject.getStatus());
    }

    @Test
    public void testGetProjectByIdSuccessful() {
        Project project = new Project();
        project.setId(1L);

        when(projectRepository.getProjectById(project.getId())).thenReturn(project);
        ProjectDto result = projectService.getProjectById(project.getId());

        assertEquals(project.getId(), result.getId());
    }

    @Test
    public void testGetProjectByIdFailed() {
        when(projectRepository.getProjectById(0L)).thenThrow(new EntityNotFoundException());
        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(0L));
    }

    @Test
    public void testGetAllProjectsSuccessfulWhenUserIsTeamMember() {
        ProjectFilterDto projectFilterDto = setupProjectsAndMocks(1L, true);

        List<ProjectDto> result = projectService.getAllProjects(projectFilterDto);
        assertEquals(2, result.size());
        verify(filter, times(2)).apply(any(), any());
    }

    @Test
    public void testGetAllProjectsSuccessfulWhenUserNotIsTeamMember() {
        ProjectFilterDto projectFilterDto = setupProjectsAndMocks(2L, false);

        List<ProjectDto> result = projectService.getAllProjects(projectFilterDto);

        assertEquals(1, result.size());
        verify(filter, times(2)).apply(any(), any());
    }

    @Test
    public void testCreateProjectSuccessful() {
        ProjectDto dto = new ProjectDto();
        doNothing().when(projectValidator).validate(any(), any(), any());

        projectService.createProject(dto);
        verify(projectRepository).create(any());
    }

    @Test
    void getProjectEntityByIdSuccess() {
        Mockito.lenient().when(projectRepository.getProjectById(Mockito.anyLong())).thenReturn(new Project());
        assertEquals(new Project(), projectService.getProjectEntityById(1L));
    }

    @Test
    public void testUpdateProjectSuccessful() {
        ProjectDto dto = new ProjectDto();
        Project project = new Project();

        doNothing().when(projectValidator).validate(any(), any(), any());
        when(projectRepository.getProjectById(dto.getId())).thenReturn(project);

        projectService.updateProject(dto);

        verify(projectMapper, times(1)).update(any(), any());
        verify(projectRepository).save(project);
    }

    @Test
    public void testExistsByOwnerUserIdAndNameSuccessful() {
        Long userId = 1L;
        String name = "name";

        projectService.existsByOwnerUserIdAndName(userId, name);
        verify(projectRepository).existsByOwnerUserIdAndName(userId, name);
    }

    @Test
    void getProjectEntityByIdFail() {
        Mockito.lenient().when(projectRepository.getProjectById(1L)).thenThrow(new EntityNotFoundException("Project not found by id: %s".formatted(1L)));
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> projectService.getProjectEntityById(1L));
        assertEquals("Project not found by id: %s".formatted(1L), exception.getMessage());
    }


    private ProjectFilterDto setupProjectsAndMocks(long userId, boolean userIsTeamMember) {
        ProjectFilterDto projectFilterDto = new ProjectFilterDto();
        Project privateProject = new Project();
        privateProject.setId(5L);
        Project publicProject = new Project();
        publicProject.setId(7L);
        Team team = new Team();
        TeamMember teamMember = new TeamMember();

        teamMember.setUserId(userId);
        team.setTeamMembers(List.of(teamMember));
        privateProject.setVisibility(ProjectVisibility.PRIVATE);
        privateProject.setTeams(List.of(team));
        publicProject.setVisibility(ProjectVisibility.PUBLIC);

        List<Project> projects = new ArrayList<>();
        projects.add(privateProject);
        projects.add(publicProject);

        when(projectRepository.findAll()).thenReturn(projects);
        when(userContext.getUserId()).thenReturn(1L);
        when(filter.isApplicable(any())).thenReturn(true);
        if (userIsTeamMember) {
            when(filter.apply(any(), any())).thenReturn(Stream.of(privateProject, publicProject));
        } else {
            when(filter.apply(any(), any())).thenReturn(Stream.of(publicProject));
        }

        return projectFilterDto;
    }

}
