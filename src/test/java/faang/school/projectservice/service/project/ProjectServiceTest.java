package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.client.CreateSubProjectDto;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.filter.project.NameFilter;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.project.StatusFilter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.project.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.update.ProjectUpdate;
import faang.school.projectservice.update.projects.ChildrenUpdate;
import faang.school.projectservice.update.projects.DescriptionUpdate;
import faang.school.projectservice.update.projects.NameUpdate;
import faang.school.projectservice.update.projects.OwnerIdUpdate;
import faang.school.projectservice.update.projects.ParentProjectUpdate;
import faang.school.projectservice.update.projects.StatusUpdate;
import faang.school.projectservice.update.projects.VisibilityUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {


    private ProjectService projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private SubProjectMapper subProjectMapper = Mappers.getMapper(SubProjectMapper.class);
    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    private List<ProjectUpdate> projectUpdates;

    private List<ProjectFilter> projectFilters;
    private Project project = new Project();
    private CreateSubProjectDto createSubProjectDto = new CreateSubProjectDto();
    private ProjectDto projectDto = new ProjectDto();
    private Project firstSubProject = new Project();
    private ProjectFilterDto projectFilterDto = new ProjectFilterDto();
    ;

    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    @BeforeEach
    void init() {
        project.setName("testName");
        createSubProjectDto.setName("secondTestName");
        projectDto.setId(8L);

        projectUpdates = List.of(
                new ChildrenUpdate(projectRepository),
                new DescriptionUpdate(),
                new NameUpdate(),
                new OwnerIdUpdate(),
                new ParentProjectUpdate(projectRepository),
                new StatusUpdate(),
                new VisibilityUpdate());

        projectFilters = List.of(
                new NameFilter(),
                new StatusFilter()
        );

        projectService = new ProjectService(
                projectRepository,
                subProjectMapper,
                projectMapper,
                projectUpdates,
                projectFilters);
    }

    @Test
    void testCreateSubProjectWithPrivateVisibility() {
        project.setVisibility(ProjectVisibility.PUBLIC);
        createSubProjectDto.setVisibility(ProjectVisibility.PRIVATE);

        Mockito.when(projectRepository.getProjectById(9L)).thenReturn(project);
        assertThrows(IllegalArgumentException.class,
                () -> projectService.createSubProject(9L, createSubProjectDto));
    }

    @Test
    void testCreateSubProjectWithSameName() {
        Project subProject = new Project();
        subProject.setName("testName");
        project.setChildren(List.of(subProject));
        project.setName("anotherName");
        createSubProjectDto.setName("testName");

        Mockito.when(projectRepository.getProjectById(9L)).thenReturn(project);
        assertThrows(IllegalArgumentException.class,
                () -> projectService.createSubProject(9L, createSubProjectDto));
    }

    @Test
    void testSuccessfulSubProject() {
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
        assertThrows(IllegalArgumentException.class,
                () -> projectService.updateSubProject(projectDto));
    }

    @Test
    void testInvalidIdForUpdateSubProject() {
        projectDto.setId(-11L);
        assertThrows(IllegalArgumentException.class,
                () -> projectService.updateSubProject(projectDto));
    }

    @Test
    void testCancelledStatusForUpdateSubProject() {
        firstSubProject.setStatus(ProjectStatus.IN_PROGRESS);

        project.setStatus(ProjectStatus.CANCELLED);
        projectDto.setId(8L);
        projectDto.setChildrenIds(List.of(1L, 1L));

        Mockito.when(projectRepository.getProjectById(8L)).thenReturn(project);
        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(firstSubProject);

        assertThrows(IllegalStateException.class,
                () -> projectService.updateSubProject(projectDto));
    }

    @Test
    void testSuccessForUpdateSubProject() {
        Mockito.when(projectRepository.getProjectById(8L)).thenReturn(project);
        projectDto.setName("Test");
        projectDto.setDescription("TestDescription");

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
        projectDto.setId(8L);
        projectDto.setChildrenIds(List.of(1L, 1L));

        Mockito.when(projectRepository.getProjectById(8L)).thenReturn(project);
        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(firstSubProject);

        projectDto.setName("Test");
        projectDto.setDescription("TestDescription");

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

        assertThrows(NullPointerException.class,
                () -> projectService.getSubProjects(8L, projectFilterDto));
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
        projectFilterDto.setProjectStatus(ProjectStatus.CREATED);

        project.setChildren(List.of(firstSubProject, secondSubProject, thirdSubProject));

        Mockito.when(projectRepository.getProjectById(8L)).thenReturn(project);
        List<ProjectDto> projectDtos = projectService.getSubProjects(8L, projectFilterDto);

        assertEquals(projectDtos.size(), 1);
        assertEquals(projectDtos.get(0).getName(), firstSubProject.getName());
        assertEquals(projectDtos.get(0).getStatus(), firstSubProject.getStatus());
    }
}