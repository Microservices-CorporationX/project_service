package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectNameFilterTest {

    ProjectNameFilter projectNameFilter;
    private ProjectFilterDto filter;

    @BeforeEach
    void setUp() {
        projectNameFilter = new ProjectNameFilter();
    }

    @Test
    @DisplayName("Filter is applicable successfully")
    void testIsApplicable() {
        filter = ProjectFilterDto.builder()
                .nameProjectPattern("test")
                .build();

        boolean result = projectNameFilter.isApplicable(filter);

        assertTrue(result);
    }

    @Test
    @DisplayName("Filter is not applicable")
    void testIsNotApplicable() {
        filter = ProjectFilterDto.builder()
                .nameProjectPattern(null)
                .build();

        boolean result = projectNameFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    @DisplayName("Apply filter successfully")
    void testApply() {
        filter = ProjectFilterDto.builder()
                .nameProjectPattern("test")
                .build();

        Stream<Project> projects = Stream.of(
                Project.builder().name("test").build(),
                Project.builder().name("another").build()
        );

        Stream<Project> result = projectNameFilter.apply(projects, filter);

        assertEquals(1, result.count());
    }

    @Test
    @DisplayName("Apply filter with empty stream")
    void testApplyEmptyStream() {
        filter = ProjectFilterDto.builder()
                .nameProjectPattern("test")
                .build();

        Stream<Project> result = projectNameFilter.apply(Stream.empty(), filter);

        assertEquals(0, result.count());
    }
}
