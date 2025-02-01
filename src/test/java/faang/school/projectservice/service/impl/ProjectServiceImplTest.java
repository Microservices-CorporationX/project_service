package faang.school.projectservice.service.impl;

import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    ProjectRepository projectRepositoryMock;
    @InjectMocks
    ProjectServiceImpl projectService;

    private Project project;

    @BeforeEach
    void setUp() {
        TeamMember teamMember1 = TeamMember.builder()
                .userId(1L)
                .build();
        TeamMember teamMember2 = TeamMember.builder()
                .userId(2L)
                .build();

        Team team = new Team();
        team.setTeamMembers(List.of(teamMember1, teamMember2));

        List<Team> teams = new ArrayList<>();
        teams.add(team);

        List<Resource> resources = new ArrayList<>();
        Resource resource1 = Resource.builder()
                .id(10001L)
                .key("key_1")
                .name("file_1")
                .build();
        Resource resource2 = Resource.builder()
                .id(10002L)
                .key("key_2")
                .name("file_2")
                .build();
        resources.add(resource1);
        resources.add(resource2);

        project = Project.builder()
                .id(1010L)
                .name("test project 10")
                .teams(teams)
                .resources(resources)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Test if user is in project")
    void testIsUserInProject() {
        long userId = 1L;
        long projectId = 222L;
        Mockito.when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(project));
        Assertions.assertTrue(projectService.isUserInProject(userId, projectId));
        userId = 33L;
        Assertions.assertFalse(projectService.isUserInProject(userId, projectId));
    }

    @Test
    @DisplayName("Test is project public or not")
    void testIsProjectPublic() {
        long projectId = 1010L;
        long privateProjectId = 1011L;
        Project privateProject = Project.builder()
                .id(1011L)
                .name("test project 11")
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        Mockito.when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(project));
        Assertions.assertTrue(projectService.isProjectPublic(projectId));
        Mockito.when(projectRepositoryMock.findById(privateProjectId)).thenReturn(Optional.ofNullable(privateProject));
        Assertions.assertFalse(projectService.isProjectPublic(privateProjectId));
    }

    @Test
    @DisplayName("Test get project resource Ids")
    void testGetProjectResourceIds() {
        long projectId = 1010L;
        Mockito.when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(project));
        List<Long> resourceIds = projectService.getProjectResourceIds(projectId);
        Assertions.assertTrue(resourceIds.containsAll(List.of(10001L, 10002L)));
        Assertions.assertFalse(resourceIds.containsAll(List.of(20001L, 20002L)));
    }
}