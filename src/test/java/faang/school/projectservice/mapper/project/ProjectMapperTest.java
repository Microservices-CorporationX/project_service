package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectCreateReq;
import faang.school.projectservice.dto.project.ProjectPatchReq;
import faang.school.projectservice.dto.project.ProjectResp;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@ActiveProfiles("test")
public class ProjectMapperTest {
    private final LocalDateTime createdAt = LocalDateTime.now();
    private final Project project1 = Project.builder()
            .name("Test1")
            .description("Test1")
            .visibility(ProjectVisibility.PRIVATE)
            .ownerId(1L)
            .build();
    private final Project project2 = Project.builder()
            .id(2L)
            .name("Test2")
            .description("Test2")
            .createdAt(createdAt)
            .status(ProjectStatus.IN_PROGRESS)
            .visibility(ProjectVisibility.PUBLIC)
            .build();

    private final Project project3 = Project.builder()
            .id(3L)
            .name("Test3")
            .description("Test3")
            .ownerId(1L)
            .createdAt(createdAt)
            .status(ProjectStatus.IN_PROGRESS)
            .visibility(ProjectVisibility.PUBLIC)
            .build();

    @Autowired
    private ProjectMapper projectMapper;

    @Test
    public void mapProjectCreateReqToProjectDtoSuccessTest() {
        ProjectCreateReq projectCreateReq = new ProjectCreateReq("Test1", "Test1", ProjectVisibility.PRIVATE, 1L);
        assertEquals(project1, projectMapper.mapProjectCreateReqToProject(projectCreateReq));
    }

    @Test
    public void mapProjectCreateReqToProjectDtoFailTest() {
        ProjectCreateReq projectCreateReq = new ProjectCreateReq("Test", "Test", ProjectVisibility.PRIVATE, 1L);
        assertNotEquals(project1, projectMapper.mapProjectCreateReqToProject(projectCreateReq));
    }

    @Test
    public void patchProjectFromProjectPatchReqSuccessTest() {
        ProjectPatchReq projectPatchReq = new ProjectPatchReq(2L, "kek", ProjectStatus.ON_HOLD);
        projectMapper.patchProjectFromProjectPatchReq(projectPatchReq, project2);
        assertEquals("kek", project2.getDescription());
        assertEquals(ProjectStatus.ON_HOLD, project2.getStatus());
    }

    @Test
    public void patchProjectFromProjectPatchReqFailTest() {
        ProjectPatchReq projectPatchReq = new ProjectPatchReq(2L, "kek", ProjectStatus.ON_HOLD);
        projectMapper.patchProjectFromProjectPatchReq(projectPatchReq, project2);
        assertNotEquals("Test2", project2.getDescription());
        assertNotEquals(ProjectStatus.IN_PROGRESS, project2.getStatus());
    }

    @Test
    public void mapProjectToProjectRespSuccessTest() {
        ProjectResp projectResp = new ProjectResp(3L, "Test3", "Test3", 1L, createdAt, ProjectStatus.IN_PROGRESS, ProjectVisibility.PUBLIC);
        assertEquals(projectResp, projectMapper.mapProjectToProjectResp(project3));
    }

    @Test
    public void mapProjectListToProjectRespListSuccessTest() {
        assertEquals(2, projectMapper.mapProjectListToProjectRespList(List.of(project2, project3)).size());
    }
}
