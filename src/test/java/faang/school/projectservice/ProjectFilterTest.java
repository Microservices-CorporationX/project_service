package faang.school.projectservice;

import faang.school.projectservice.filters.ProjectFilter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectFilterTest {

    @Test
    void testFilterAppliesCorrectly() {
        Project project = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("Sample project")
                .ownerId(100L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .build();

        ProjectFilter filter = new ProjectFilter("Test Project", ProjectStatus.CREATED, ProjectVisibility.PUBLIC);
        assertThat(filter.apply(project)).isTrue();

        filter = new ProjectFilter("Other Project", ProjectStatus.CREATED, ProjectVisibility.PUBLIC);
        assertThat(filter.apply(project)).isFalse();

        filter = new ProjectFilter("Test Project", null, ProjectVisibility.PUBLIC);
        assertThat(filter.apply(project)).isTrue();

        filter = new ProjectFilter("Test Project", ProjectStatus.CREATED, null);
        assertThat(filter.apply(project)).isTrue();
    }
    @Test
    void testAllFields() {
        Project project = Project.builder()

                .name("Test Project")

                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)

                .build();

        ProjectFilter filter = new ProjectFilter("Test Project", ProjectStatus.CREATED, ProjectVisibility.PUBLIC);
        assertThat(filter.apply(project)).isTrue();
    }
}
