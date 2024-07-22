package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.ProjectFilter;
import faang.school.projectservice.service.updater.ProjectDescriptionUpdater;
import faang.school.projectservice.service.updater.ProjectStatusUpdater;
import faang.school.projectservice.service.updater.ProjectUpdater;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    private ProjectRepository projectRepository;
    private ProjectMapper projectMapper;
    private ProjectFilter projectFilter;
    private ProjectService projectService;
    private final long projectId = 10L;
    private final long ownerId = 1L;
    private final String projectName = "Name";
    private final String description = "Cool project";
    private final ProjectStatus status = ProjectStatus.CREATED;
    private UserContext userContext;
    private Project project;
    private ProjectDto projectDto;
    List<ProjectFilter> filters;
    List<ProjectUpdater> updaters;

    @BeforeEach
    public void init() {
        projectRepository = Mockito.mock(ProjectRepository.class);
        projectMapper = Mockito.spy(ProjectMapperImpl.class);
        projectFilter = Mockito.spy(ProjectFilter.class);
        userContext = Mockito.mock(UserContext.class);

        ProjectFilter filter1 = Mockito.mock(ProjectFilter.class);
        ProjectFilter filter2 = Mockito.mock(ProjectFilter.class);
        filters = List.of(filter1, filter2);

        ProjectDescriptionUpdater updater1 = new ProjectDescriptionUpdater();
        ProjectStatusUpdater updater2 = new ProjectStatusUpdater();
        updaters = List.of(updater1, updater2);

        projectService = new ProjectService(projectRepository, projectMapper, filters, updaters, userContext);

        project = Project.builder()
                .id(projectId)
                .ownerId(ownerId)
                .name(projectName)
                .description(description)
                .status(status)
                .build();
        projectDto = ProjectDto.builder()
                .id(projectId)
                .ownerId(ownerId)
                .name(projectName)
                .description(description)
                .status(status)
                .build();
    }

    @Test
    public void testCreateAlreadyExists() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(ownerId, projectName)).thenReturn(true);

        Assertions.assertThrows(RuntimeException.class, () -> projectService.create(projectDto));
    }

    @Test
    public void testCreateWithoutOwnerId() {
        projectDto.setOwnerId(null);
        Mockito.when(userContext.getUserId()).thenReturn(ownerId);
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(ownerId, projectName)).thenReturn(false);
        Mockito.when(projectRepository.save(project)).thenReturn(project);
        Mockito.when(projectMapper.toDto(project)).thenReturn(projectDto);

        Assertions.assertEquals(projectService.create(projectDto), projectDto);
        Mockito.verify(projectRepository).save(project);
    }

    @Test
    public void testCreate() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(ownerId, projectName)).thenReturn(false);
        Mockito.when(projectRepository.save(project)).thenReturn(project);
        Mockito.when(projectMapper.toDto(project)).thenReturn(projectDto);

        Assertions.assertEquals(projectService.create(projectDto), projectDto);
        Mockito.verify(projectRepository).save(project);
    }

    @Test
    public void testUpdateProjectNotExist() {
        Mockito.when(projectRepository.getProjectById(projectId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(RuntimeException.class, () -> projectService.update(projectDto));
    }

    @Test
    public void testUpdate() {
        String newDescription = "new";
        ProjectDto updater = ProjectDto.builder().id(projectId).description(newDescription).build();

        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Mockito.when(projectRepository.save(project)).thenReturn(project);

        ProjectDto result = projectService.update(updater);
        projectDto.setDescription(newDescription);
        Assertions.assertEquals(project.getDescription(), newDescription);
        Assertions.assertEquals(projectDto, result);

        Mockito.verify(projectRepository).getProjectById(projectId);
        Mockito.verify(projectRepository).save(project);
        Mockito.verify(projectMapper).toDto(project);
    }

    @Test
    public void testGetProjectsWithFilters() {
        Project publicProject = new Project();
        publicProject.setId(1L);
        publicProject.setVisibility(ProjectVisibility.PUBLIC);
        ProjectDto publicProjectDto = new ProjectDto();
        publicProjectDto.setId(1L);

        Project privateProject = new Project();
        privateProject.setId(2L);
        privateProject.setVisibility(ProjectVisibility.PRIVATE);
        ProjectDto privateProjectDto = new ProjectDto();
        privateProjectDto.setId(2L);

        Team team = new Team();
        TeamMember member = new TeamMember();
        member.setId(1L);
        team.setTeamMembers(List.of(member));
        privateProject.setTeams(List.of(team));

        ProjectFilterDto filter = new ProjectFilterDto();
        Mockito.when(projectMapper.toFilter(projectDto)).thenReturn(filter);
        Mockito.when(userContext.getUserId()).thenReturn(1L);
        Mockito.when(projectRepository.findAll()).thenReturn(List.of(publicProject, privateProject));
        Mockito.when(filters.get(0).isApplicable(filter)).thenReturn(true);
        Mockito.when(filters.get(1).isApplicable(filter)).thenReturn(false);
        Mockito.when(filters.get(0).apply(Mockito.any(), Mockito.eq(filter)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<ProjectDto> result = projectService.getProjectsWithFilters(projectDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(publicProjectDto));
        Assertions.assertTrue(result.contains(privateProjectDto));

        Mockito.verify(projectRepository, Mockito.times(2)).findAll();
        Mockito.verify(filters.get(0), Mockito.times(1)).isApplicable(filter);
        Mockito.verify(filters.get(0), Mockito.times(1)).apply(Mockito.any(), Mockito.eq(filter));
        Mockito.verify(filters.get(1), Mockito.times(1)).isApplicable(filter);
        Mockito.verify(filters.get(1), Mockito.times(0)).apply(Mockito.any(), Mockito.eq(filter));
        Mockito.verify(projectMapper, Mockito.times(1)).toDto(publicProject);
        Mockito.verify(projectMapper, Mockito.times(1)).toDto(privateProject);
    }

    @Test
    public void testGetAllProjects() {
        Mockito.when(projectRepository.findAll()).thenReturn(List.of(project));
        List<ProjectDto> result = projectService.getAllProjects();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.contains(projectDto));

        Mockito.verify(projectRepository).findAll();
        Mockito.verify(projectMapper).toDto(project);
    }

    @Test
    public void testGetProjectByIdNotExist() {
        Mockito.when(projectRepository.getProjectById(projectId)).thenThrow(new EntityNotFoundException());
        Assertions.assertThrows(RuntimeException.class, () -> projectService.getProjectById(projectDto));
    }

    @Test
    public void testGetProjectById() {
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Assertions.assertEquals(projectDto, projectService.getProjectById(projectDto));
        Mockito.verify(projectRepository).getProjectById(projectId);
    }
}
