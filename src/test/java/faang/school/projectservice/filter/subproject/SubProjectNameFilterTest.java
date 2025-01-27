package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubProjectNameFilterTest {
    private final SubProjectNameFilter subProjectNameFilter = new SubProjectNameFilter();

    @Test
    public void testisNotApplicable() {
        SubProjectFilterDto filterDto = new SubProjectFilterDto();

        assertFalse(subProjectNameFilter.isApplicable(filterDto));
    }

    @Test
    public void testisApplicable() {
        SubProjectFilterDto filterDto = new SubProjectFilterDto();
        filterDto.setName("Name");

        assertTrue(subProjectNameFilter.isApplicable(filterDto));
    }

    @Test
    public void testFilterEntityWithoutContainingName() {
        Project project = new Project();
        project.setName("Alex");
        SubProjectFilterDto filterDto = new SubProjectFilterDto();
        filterDto.setName("Name");

        assertFalse(subProjectNameFilter.filterEntity(project, filterDto));
    }

    @Test
    public void testFilterEntityWithContainingName() {
        Project project = new Project();
        project.setName("Name1234");
        SubProjectFilterDto filterDto = new SubProjectFilterDto();
        filterDto.setName("Name");

        assertTrue(subProjectNameFilter.filterEntity(project, filterDto));
    }
}
