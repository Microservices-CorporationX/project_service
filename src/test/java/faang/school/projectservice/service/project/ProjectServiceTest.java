package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectCreateReq;
import faang.school.projectservice.dto.project.ProjectFiltersReq;
import faang.school.projectservice.dto.project.ProjectPatchReq;
import faang.school.projectservice.dto.project.ProjectResp;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProjectSuccessTest() {
        ProjectCreateReq projectCreateReq = new ProjectCreateReq("Project", "Description", ProjectVisibility.PUBLIC, 1L);
        Project project = Project.builder()
                .name(projectCreateReq.name())
                .description(projectCreateReq.description())
                .visibility(projectCreateReq.visibility())
                .ownerId(projectCreateReq.ownerId())
                .build();
        when(projectRepository.existsByOwnerUserIdAndName(1L, "Project")).thenReturn(false);
        when(projectMapper.mapProjectCreateReqToProject(projectCreateReq)).thenReturn(project);
        assertDoesNotThrow(() -> projectService.createProject(projectCreateReq));
        verify(projectRepository, times(1)).existsByOwnerUserIdAndName(1L, "Project");
        verify(projectMapper, times(1)).mapProjectCreateReqToProject(projectCreateReq);
    }

    @Test
    void createProjectWithAlreadyExistentNameForOwnerIdFailTest() {
        ProjectCreateReq projectCreateReq = new ProjectCreateReq("Project", "Description", ProjectVisibility.PUBLIC, 1L);
        when(projectRepository.existsByOwnerUserIdAndName(1L, "Project")).thenReturn(true);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> projectService.createProject(projectCreateReq));
        String expectedMessage = "Project with name: Project already exists for this owner id: 1";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(projectRepository, times(1)).existsByOwnerUserIdAndName(1L, "Project");
        verify(projectMapper, times(0)).mapProjectCreateReqToProject(projectCreateReq);
    }

    @Test
    void patchProjectSuccessTest() {
        ProjectPatchReq projectPatchReq = new ProjectPatchReq(1L, "Description", ProjectStatus.ON_HOLD);
        Project project = Project.builder()
                .id(projectPatchReq.id())
                .description(projectPatchReq.description())
                .status(projectPatchReq.status())
                .build();
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        assertDoesNotThrow(() -> projectService.patchProject(projectPatchReq));
        verify(projectRepository, times(1)).getProjectById(1L);
        verify(projectMapper, times(1)).patchProjectFromProjectPatchReq(projectPatchReq, project);
    }

    @Test
    void patchProjectForNonExistentProjectFailTest() {
        ProjectPatchReq projectPatchReq = new ProjectPatchReq(1L, "Description", ProjectStatus.ON_HOLD);
        Project project = Project.builder()
                .id(projectPatchReq.id())
                .description(projectPatchReq.description())
                .status(projectPatchReq.status())
                .build();
        when(projectRepository.getProjectById(1L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> projectService.patchProject(projectPatchReq));
        verify(projectRepository, times(1)).getProjectById(1L);
        verify(projectMapper, times(0)).patchProjectFromProjectPatchReq(projectPatchReq, project);
    }

    @Test
    void findProjectsWithFullFiltersSuccessTest() {
        LocalDateTime createdAt = LocalDateTime.now();
        Project project1 = Project.builder()
                .id(3L)
                .name("Test3")
                .description("Test3")
                .createdAt(createdAt)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(Team.builder()
                        .teamMembers(List.of(TeamMember.builder()
                                .userId(1L)
                                .build()))
                        .build()))
                .build();
        Project project2 = Project.builder()
                .id(2L)
                .name("Test2")
                .description("Test2")
                .createdAt(createdAt)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        ProjectResp projectResp = new ProjectResp(3L, "Test3", "Test3", 1L, createdAt, ProjectStatus.IN_PROGRESS, ProjectVisibility.PRIVATE);
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        when(projectMapper.mapProjectListToProjectRespList(List.of(project1))).thenReturn(List.of(projectResp));
        when(userContext.getUserId()).thenReturn(1L);
        assertDoesNotThrow(() -> {
            List<ProjectResp> projects = projectService.findProjectsWithFilters(new ProjectFiltersReq("Test3", ProjectStatus.IN_PROGRESS));
            assertEquals(1, projects.size());
            assertEquals(3, projects.get(0).id());
            assertEquals("Test3", projects.get(0).name());
            assertEquals("Test3", projects.get(0).description());
            assertEquals(1, projects.get(0).ownerId());
            assertEquals(createdAt, projects.get(0).createdAt());
            assertEquals(ProjectStatus.IN_PROGRESS, projects.get(0).status());
            assertEquals(ProjectVisibility.PRIVATE, projects.get(0).visibility());
        });
        verify(projectRepository, times(1)).findAll();
        verify(projectMapper, times(1)).mapProjectListToProjectRespList(List.of(project1));
        verify(userContext, times(1)).getUserId();
    }

    @Test
    void findProjectsWithOnlyUserIdFilterSuccessTest() {
        LocalDateTime createdAt = LocalDateTime.now();
        Project project1 = Project.builder()
                .id(3L)
                .name("Test3")
                .description("Test3")
                .createdAt(createdAt)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(Team.builder()
                        .teamMembers(List.of(TeamMember.builder()
                                .userId(1L)
                                .build()))
                        .build()))
                .build();
        Project project2 = Project.builder()
                .id(2L)
                .name("Test2")
                .description("Test2")
                .createdAt(createdAt)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        ProjectResp projectResp1 = new ProjectResp(3L, "Test3", "Test3", 1L, createdAt, ProjectStatus.IN_PROGRESS, ProjectVisibility.PRIVATE);
        ProjectResp projectResp2 = new ProjectResp(2L, "Test2", "Test2", 1L, createdAt, ProjectStatus.IN_PROGRESS, ProjectVisibility.PUBLIC);
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        when(projectMapper.mapProjectListToProjectRespList(List.of(project1, project2))).thenReturn(List.of(projectResp1, projectResp2));
        when(userContext.getUserId()).thenReturn(1L);
        assertDoesNotThrow(() -> {
            List<ProjectResp> projects = projectService.findProjectsWithFilters(new ProjectFiltersReq(null, null));
            assertEquals(2, projects.size());
            assertEquals("Test3", projects.get(0).name());
            assertEquals(ProjectStatus.IN_PROGRESS, projects.get(0).status());
            assertEquals(ProjectVisibility.PRIVATE, projects.get(0).visibility());
            assertEquals("Test2", projects.get(1).name());
            assertEquals(ProjectStatus.IN_PROGRESS, projects.get(1).status());
            assertEquals(ProjectVisibility.PUBLIC, projects.get(1).visibility());
        });
        verify(projectRepository, times(1)).findAll();
        verify(projectMapper, times(1)).mapProjectListToProjectRespList(List.of(project1, project2));
        verify(userContext, times(1)).getUserId();
    }

    @Test
    void findProjectsWithFiltersEmptyResultSuccessTest() {
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());
        when(projectMapper.mapProjectListToProjectRespList(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(userContext.getUserId()).thenReturn(1L);
        assertDoesNotThrow(() -> {
            List<ProjectResp> projects = projectService.findProjectsWithFilters(new ProjectFiltersReq("Test", ProjectStatus.CREATED));
            assertEquals(0, projects.size());
        });
        verify(projectRepository, times(1)).findAll();
        verify(projectMapper, times(1)).mapProjectListToProjectRespList(Collections.emptyList());
        verify(userContext, times(1)).getUserId();
    }

    @Test
    void findProjectsSuccessTest() {
        LocalDateTime createdAt = LocalDateTime.now();
        Project project1 = Project.builder()
                .id(3L)
                .name("Test3")
                .description("Test3")
                .createdAt(createdAt)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(Team.builder()
                        .teamMembers(List.of(TeamMember.builder()
                                .userId(1L)
                                .build()))
                        .build()))
                .build();
        Project project2 = Project.builder()
                .id(2L)
                .name("Test2")
                .description("Test2")
                .createdAt(createdAt)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        ProjectResp projectResp1 = new ProjectResp(3L, "Test3", "Test3", 1L, createdAt, ProjectStatus.IN_PROGRESS, ProjectVisibility.PRIVATE);
        ProjectResp projectResp2 = new ProjectResp(2L, "Test2", "Test2", 2L, createdAt, ProjectStatus.IN_PROGRESS, ProjectVisibility.PUBLIC);
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        when(projectMapper.mapProjectListToProjectRespList(List.of(project1, project2))).thenReturn(List.of(projectResp1, projectResp2));
        assertDoesNotThrow(() -> {
            List<ProjectResp> projects = projectService.findProjects();
            assertEquals(2, projects.size());
            assertEquals("Test3", projects.get(0).name());
            assertEquals(ProjectStatus.IN_PROGRESS, projects.get(0).status());
            assertEquals(ProjectVisibility.PRIVATE, projects.get(0).visibility());
            assertEquals("Test2", projects.get(1).name());
            assertEquals(ProjectStatus.IN_PROGRESS, projects.get(1).status());
            assertEquals(ProjectVisibility.PUBLIC, projects.get(1).visibility());
        });
        verify(projectRepository, times(1)).findAll();
        verify(projectMapper, times(1)).mapProjectListToProjectRespList(List.of(project1, project2));
    }

    @Test
    void findProjectByIdSuccessTest() {
        LocalDateTime createdAt = LocalDateTime.now();
        Project project = Project.builder()
                .id(3L)
                .name("Test3")
                .description("Test3")
                .createdAt(createdAt)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        ProjectResp projectResp = new ProjectResp(3L, "Test3", "Test3", 1L, createdAt, ProjectStatus.IN_PROGRESS, ProjectVisibility.PRIVATE);
        when(projectRepository.getProjectById(3L)).thenReturn(project);
        when(projectMapper.mapProjectToProjectResp(project)).thenReturn(projectResp);
        assertDoesNotThrow(() -> {
            ProjectResp projectById = projectService.findProjectById(3L);
            assertEquals(3, projectById.id());
            assertEquals("Test3", projectById.name());
            assertEquals("Test3", projectById.description());
            assertEquals(1, projectById.ownerId());
            assertEquals(createdAt, projectById.createdAt());
            assertEquals(ProjectStatus.IN_PROGRESS, projectById.status());
            assertEquals(ProjectVisibility.PRIVATE, projectById.visibility());
        });
        verify(projectRepository, times(1)).getProjectById(3L);
        verify(projectMapper, times(1)).mapProjectToProjectResp(project);
    }

    @Test
    void findProjectByIdFailTest() {
        when(projectRepository.getProjectById(300L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> projectService.findProjectById(300L));
    }
}
