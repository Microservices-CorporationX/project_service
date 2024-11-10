package faang.school.projectservice.update.projects;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DescriptionUpdateTest {

    private DescriptionUpdate descriptionUpdate = new DescriptionUpdate();
    private ProjectDto projectDto = new ProjectDto();
    private Project project = new Project();

    @Test
    void testIsApplicableFalse() {
        assertFalse(descriptionUpdate.isApplicable(projectDto));
    }

    @Test
    void testIsApplicableTrue() {
        projectDto.setDescription("Description");
        assertTrue(descriptionUpdate.isApplicable(projectDto));
    }

    @Test
    void testApply() {
        projectDto.setDescription("Description");

        descriptionUpdate.apply(project, projectDto);
        assertEquals(project.getDescription(), projectDto.getDescription());
    }
}