package school.faang.project_service.service.filters;

import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.filters.StatusProjectFilter;
import faang.school.projectservice.service.filters.VisibilityProjectFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VisibilityProjectFilterTest {

    private VisibilityProjectFilter visibilityFilter;
    private FilterSubProjectDto filterDto;

    @BeforeEach
    public void init() {
        visibilityFilter = new VisibilityProjectFilter();
        filterDto = new FilterSubProjectDto("name", ProjectStatus.CREATED, ProjectVisibility.PRIVATE);
    }

    @Test
    public void testIsNotApplicable() {
        assertFalse(visibilityFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicable() {
        //filterDto.status(ProjectStatus.IN_PROGRESS);
        assertTrue(visibilityFilter.isApplicable(filterDto));
    }

    @Test
    public void testApplyFilter() {
        //filterDto.setStatus(ProjectStatus.CANCELLED);
        Stream<Project> requests = Stream.of(
                Project.builder().visibility(ProjectVisibility.PUBLIC).build(),
                Project.builder().visibility(ProjectVisibility.PRIVATE).build(),
                Project.builder().visibility(ProjectVisibility.PUBLIC).build());

        List<Project> result = visibilityFilter.apply(requests, filterDto).toList();
        assertEquals(1, result.size());
    }
}

