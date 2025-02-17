package ru.corporationx.projectservice.filters.project;

import org.junit.jupiter.api.Test;
import ru.corporationx.projectservice.filters.project.StatusProjectFilter;
import ru.corporationx.projectservice.model.dto.filter.ProjectFilterDto;
import ru.corporationx.projectservice.model.entity.Project;
import ru.corporationx.projectservice.model.entity.ProjectStatus;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectStatusFilterTest {

    private final StatusProjectFilter projectStatusFilter = new StatusProjectFilter();

    @Test
    public void testIsApplicable(){
        ProjectFilterDto positive = ProjectFilterDto.builder().statusPattern(ProjectStatus.IN_PROGRESS).build();
        ProjectFilterDto negative = ProjectFilterDto.builder().build();
        assertTrue(projectStatusFilter.isApplicable(positive));
        assertFalse(projectStatusFilter.isApplicable(negative));
    }

    @Test
    public void testApply(){
        Project project1 = Project.builder().status(ProjectStatus.IN_PROGRESS).build();
        Project project2 = Project.builder().status(ProjectStatus.IN_PROGRESS).build();
        Project project3 = Project.builder().status(ProjectStatus.COMPLETED).build();
        Project project4 = Project.builder().status(ProjectStatus.ON_HOLD).build();
        ProjectFilterDto filter = ProjectFilterDto.builder().statusPattern(ProjectStatus.IN_PROGRESS).build();
        Stream<Project> result = projectStatusFilter.apply(Stream.of(project1, project2, project3, project4), filter);
        Stream<Project> expected = Stream.of(project1, project2);
        assertEquals(expected.toList(), result.toList());
    }
}
