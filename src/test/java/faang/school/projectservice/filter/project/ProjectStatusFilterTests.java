package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectStatusFilterTests {

    private final ProjectStatusFilter projectStatusFilter = new ProjectStatusFilter();

    @Test
    public void testIsApplicable(){
        ProjectFilterDto positive = ProjectFilterDto.builder().status(ProjectStatus.IN_PROGRESS).build();
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
        ProjectFilterDto filter = ProjectFilterDto.builder().status(ProjectStatus.IN_PROGRESS).build();
        Stream<Project> result = projectStatusFilter.apply(Stream.of(project1, project2, project3, project4), filter);
        Stream<Project> expected = Stream.of(project1, project2);
        assertEquals(expected.toList(), result.toList());
    }
}
