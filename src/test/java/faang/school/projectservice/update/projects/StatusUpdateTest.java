package faang.school.projectservice.update.projects;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusUpdateTest {
    private StatusUpdate statusUpdate = new StatusUpdate();
    private ProjectDto projectDto = new ProjectDto();
    private Project project = new Project();

    @Test
    void testIsApplicableFalse() {
        assertFalse(statusUpdate.isApplicable(projectDto));
    }

    @Test
    void testIsApplicableTrue() {
        projectDto.setStatus(ProjectStatus.IN_PROGRESS);
        assertTrue(statusUpdate.isApplicable(projectDto));
    }

    @Test
    void testApply() {
        projectDto.setStatus(ProjectStatus.IN_PROGRESS);

        statusUpdate.apply(project, projectDto);
        assertEquals(project.getStatus(), projectDto.getStatus());
    }
}