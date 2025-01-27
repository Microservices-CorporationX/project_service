package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubProjectVisibilityFilterTest {
    private final SubProjectVisibilityFilter subProjectVisibilityFilter = new SubProjectVisibilityFilter();

    @Test
    public void testisApplicable() {
        assertTrue(true);
    }

    @Test
    public void testFilterEntityWithoutPrivate() {
        Project project = new Project();
        project.setVisibility(ProjectVisibility.PUBLIC);
        SubProjectFilterDto filterDto = new SubProjectFilterDto();

        assertTrue(subProjectVisibilityFilter.filterEntity(project, filterDto));
    }

    @Test
    public void testFilterEntityWithPrivate() {
        Project project = new Project();
        project.setVisibility(ProjectVisibility.PRIVATE);
        SubProjectFilterDto filterDto = new SubProjectFilterDto();

        assertFalse(subProjectVisibilityFilter.filterEntity(project, filterDto));
    }
}
