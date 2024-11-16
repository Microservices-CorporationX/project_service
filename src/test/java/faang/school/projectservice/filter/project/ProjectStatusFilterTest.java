package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectStatusFilterTest {

    ProjectStatusFilter projectStatusFilter;
    private ProjectFilterDto filter;

    @BeforeEach
    void setUp() {
        projectStatusFilter = new ProjectStatusFilter();
    }

    @Test
    @DisplayName("Filter is applicable successfully")
    void testIsApplicable() {
        filter = ProjectFilterDto.builder()
                .status(ProjectStatus.CREATED)
                .build();

        boolean result = projectStatusFilter.isApplicable(filter);

        assertTrue(result);
    }

    @Test
    @DisplayName("Filter is not applicable")
    void testIsNotApplicable() {
        filter = ProjectFilterDto.builder()
                .status(null)
                .build();

        boolean result = projectStatusFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    @DisplayName("Apply filter successfully")
    void testApply() {
        filter = ProjectFilterDto.builder()
                .status(ProjectStatus.CREATED)
                .build();

        Stream<Project> projects = Stream.of(
                Project.builder().status(ProjectStatus.CREATED).build(),
                Project.builder().status(ProjectStatus.CANCELLED).build()
        );

        Stream<Project> result = projectStatusFilter.apply(projects, filter);

        assertEquals(1, result.count());
    }

    @Test
    @DisplayName("Apply filter with empty stream")
    void testApplyEmptyStream() {
        filter = ProjectFilterDto.builder()
                .status(ProjectStatus.CREATED)
                .build();

        Stream<Project> projects = Stream.empty();

        Stream<Project> result = projectStatusFilter.apply(projects, filter);

        assertEquals(0, result.count());
    }
}
