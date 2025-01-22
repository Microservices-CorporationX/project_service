package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubProjectStatusFilterTest {
    private final SubProjectStatusFilter subProjectStatusFilter = new SubProjectStatusFilter();

    @Test
    public void testisNotApplicable() {
        SubProjectFilterDto filterDto = new SubProjectFilterDto();

        assertFalse(subProjectStatusFilter.isApplicable(filterDto));
    }

    @Test
    public void testisApplicable() {
        SubProjectFilterDto filterDto = new SubProjectFilterDto();
        filterDto.setStatus(ProjectStatus.COMPLETED);

        assertTrue(subProjectStatusFilter.isApplicable(filterDto));
    }

    @Test
    public void testFilterEntityWithoutSameStatus() {
        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);
        SubProjectFilterDto filterDto = new SubProjectFilterDto();
        filterDto.setStatus(ProjectStatus.COMPLETED);

        assertFalse(subProjectStatusFilter.filterEntity(project, filterDto));
    }

    @Test
    public void testFilterEntityWithSameStatus() {
        Project project = new Project();
        project.setStatus(ProjectStatus.COMPLETED);
        SubProjectFilterDto filterDto = new SubProjectFilterDto();
        filterDto.setStatus(ProjectStatus.COMPLETED);

        assertTrue(subProjectStatusFilter.filterEntity(project, filterDto));
    }
}
