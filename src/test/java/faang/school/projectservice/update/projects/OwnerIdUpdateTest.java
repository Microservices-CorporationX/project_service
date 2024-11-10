package faang.school.projectservice.update.projects;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OwnerIdUpdateTest {
    private OwnerIdUpdate nameUpdate = new OwnerIdUpdate();
    private ProjectDto projectDto = new ProjectDto();
    private Project project = new Project();

    @Test
    void testIsApplicableFalse() {
        assertFalse(nameUpdate.isApplicable(projectDto));
    }

    @Test
    void testIsApplicableTrue() {
        projectDto.setOwnerId(1L);
        assertTrue(nameUpdate.isApplicable(projectDto));
    }

    @Test
    void testApply() {
        projectDto.setOwnerId(1L);

        nameUpdate.apply(project, projectDto);
        assertEquals(project.getOwnerId(), projectDto.getOwnerId());
    }
}