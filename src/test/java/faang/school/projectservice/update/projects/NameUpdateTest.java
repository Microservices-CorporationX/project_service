package faang.school.projectservice.update.projects;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameUpdateTest {

    private NameUpdate nameUpdate = new NameUpdate();
    private ProjectDto projectDto = new ProjectDto();
    private Project project = new Project();

    @Test
    void testIsApplicableFalse() {
        assertFalse(nameUpdate.isApplicable(projectDto));
    }

    @Test
    void testIsApplicableTrue() {
        projectDto.setName("Name");
        assertTrue(nameUpdate.isApplicable(projectDto));
    }

    @Test
    void testApply() {
        projectDto.setName("Name");

        nameUpdate.apply(project, projectDto);
        assertEquals(project.getName(), projectDto.getName());
    }
}