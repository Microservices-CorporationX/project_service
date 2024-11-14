package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private ProjectValidator projectValidator;

    @Spy
    private ProjectMapperImpl projectMapper;

    @Mock
    private ProjectFilter filter;

    @BeforeEach
    public void setUp() {
        List<ProjectFilter> projectFilters = List.of(filter, filter);

        projectService = new ProjectService(
                projectRepository,
                userContext,
                projectValidator,
                projectMapper,
                projectFilters
        );
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
        assertThrows(EntityNotFoundException.class,
                () -> projectService.getProjectById(0L));
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
