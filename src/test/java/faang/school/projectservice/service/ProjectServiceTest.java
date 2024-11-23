package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.StorageSizeExceededException;
import faang.school.projectservice.filters.project.NameProjectFilter;
import faang.school.projectservice.filters.project.ProjectFilter;
import faang.school.projectservice.filters.project.StatusProjectFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    private static final String NOT_OWNER_UPDATING_MESSAGE = "The user is not the owner of this project";

    @Mock
    private UserContext userContext;
    @Spy
    private ProjectMapperImpl projectMapper;
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private NameProjectFilter nameProjectFilter;
    @Spy
    private StatusProjectFilter statusProjectFilter;
    @InjectMocks
    private ProjectService projectService;


    @BeforeEach
    void setUp() {
        List<ProjectFilter> filters = List.of(nameProjectFilter, statusProjectFilter);
        projectService = new ProjectService(userContext, projectMapper, projectRepository, filters);
    }

    @Test
    void testCreateWithExistingName() {
        String name = "same name";
        long userId = 1L;
        ProjectDto projectDto = ProjectDto.builder().name(name).build();
        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.existsByOwnerUserIdAndName(userId, name)).thenReturn(true);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectService.create(projectDto));
        String expectedMessage = "Can not create new project with this project name, " +
                "this name is already used for another project of this user";
        assertEquals(expectedMessage, exception.getMessage());
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
        Project project = Project.builder().ownerId(userId).status(ProjectStatus.CREATED).build();
        when(projectRepository.getProjectById(projectId))
                .thenReturn(project);
        ProjectDto expectedDto = projectMapper.toDto(project);
        expectedDto.setStatus(ProjectStatus.IN_PROGRESS);
        Project updatedProject = projectMapper.toEntity(expectedDto);
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
        Project project = Project.builder().ownerId(userId).description("Old").build();
        when(projectRepository.getProjectById(projectId))
                .thenReturn(project);
        ProjectDto expectedDto = projectMapper.toDto(project);
        expectedDto.setDescription("New");
        Project updatedProject = projectMapper.toEntity(expectedDto);
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
                .visibility(ProjectVisibility.PUBLIC).id(1L).ownerId(2L).name("name").status(ProjectStatus.CREATED)
                .build();
        Project project2 = Project.builder()
                .visibility(ProjectVisibility.PUBLIC).id(2L).ownerId(2L).name("name").status(ProjectStatus.CANCELLED)
                .build();
        Project project3 = Project.builder()
                .visibility(ProjectVisibility.PUBLIC).id(3L).ownerId(1L).name("no").status(ProjectStatus.CREATED)
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
                .visibility(ProjectVisibility.PRIVATE).id(1L).ownerId(1L).name("name").status(ProjectStatus.CREATED)
                .build();
        Project project2 = Project.builder()
                .visibility(ProjectVisibility.PRIVATE).id(2L).ownerId(2L).name("name").status(ProjectStatus.CREATED)
                .build();
        Team team = Team.builder().teamMembers(List.of(TeamMember.builder().userId(1L).build())).build();
        Project project3 = Project.builder()
                .visibility(ProjectVisibility.PRIVATE).id(3L).ownerId(2L).teams(List.of(team)).name("name")
                .status(ProjectStatus.CREATED)
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
                .visibility(ProjectVisibility.PRIVATE).id(1L).ownerId(1L).name("name").status(ProjectStatus.CREATED)
                .build();
        Project project2 = Project.builder()
                .visibility(ProjectVisibility.PRIVATE).id(2L).ownerId(2L).name("name").status(ProjectStatus.CREATED)
                .build();
        Team team = Team.builder().teamMembers(List.of(TeamMember.builder().userId(1L).build())).build();
        Project project3 = Project.builder()
                .visibility(ProjectVisibility.PRIVATE).id(3L).ownerId(2L).teams(List.of(team)).name("name")
                .status(ProjectStatus.CREATED)
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
        Project expected = Project.builder().ownerId(2L).visibility(ProjectVisibility.PUBLIC).build();
        when(projectRepository.getProjectById(1L))
                .thenReturn(expected);
        Optional<ProjectDto> actual = projectService.findById(1L);
        assertTrue(actual.isPresent());
        assertEquals(projectMapper.toDto(expected), actual.get());
    }

    @Test
    void testChangeStorageSizeChanged() {
        long projectId = 1L;
        BigInteger storageSize = BigInteger.valueOf(2000L);
        BigInteger maxStorageSize = BigInteger.valueOf(2000000000L);
        BigInteger sizeToAdd = BigInteger.valueOf(1000L);
        BigInteger newStorageSize = storageSize.add(sizeToAdd);
        Project project = Project.builder().storageSize(storageSize).maxStorageSize(maxStorageSize).build();
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Project expected = Project.builder().storageSize(newStorageSize).maxStorageSize(maxStorageSize).build();
        when(projectRepository.save(project)).thenReturn(project);
        assertEquals(expected, projectService.changeStorageSize(projectId, sizeToAdd.longValue()));
    }

    @Test
    void testChangeStorageSizeWithSizeExceeded() {
        long projectId = 1L;
        BigInteger storageSize = BigInteger.valueOf(1800L);
        BigInteger maxStorageSize = BigInteger.valueOf(2000L);
        BigInteger sizeToAdd = BigInteger.valueOf(1000L);
        Project project = Project.builder().storageSize(storageSize).maxStorageSize(maxStorageSize).build();
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Executable executable = () -> projectService.changeStorageSize(projectId, sizeToAdd.longValue());
        StorageSizeExceededException exception = assertThrows(StorageSizeExceededException.class, executable);
        assertEquals("Storage size exceeded", exception.getMessage());
    }
}