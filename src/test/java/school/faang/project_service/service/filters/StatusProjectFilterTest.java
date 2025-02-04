package school.faang.project_service.service.filters;

import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.filters.StatusProjectFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class StatusProjectFilterTest  {

    private StatusProjectFilter statusFilter;
    private FilterSubProjectDto filterDto;

    @BeforeEach
    public void init() {
        statusFilter = new StatusProjectFilter();
        filterDto = new FilterSubProjectDto("name", ProjectStatus.CREATED, ProjectVisibility.PUBLIC);
    }

    @Test
    public void testIsNotApplicable() {
        assertFalse(statusFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicable() {
        //filterDto.status(ProjectStatus.IN_PROGRESS);
        assertTrue(statusFilter.isApplicable(filterDto));
    }

    @Test
    public void testApplyFilter() {
        //filterDto.setStatus(ProjectStatus.CANCELLED);
        Stream<Project> projects = Stream.of(
                Project.builder().status(ProjectStatus.CREATED).build(),
                Project.builder().status(ProjectStatus.IN_PROGRESS).build(),
                Project.builder().status(ProjectStatus.COMPLETED).build());

        List<Project> result = statusFilter.apply(projects, filterDto).toList();
        assertEquals(1, result.size());
    }
}
