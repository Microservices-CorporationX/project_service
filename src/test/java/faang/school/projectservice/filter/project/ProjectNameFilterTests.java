package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectNameFilterTests {

    private final ProjectNameFilter projectNameFilter = new ProjectNameFilter();

    @Test
    public void testIsApplicable(){
        ProjectFilterDto positive = ProjectFilterDto.builder().namePattern("Test").build();
        ProjectFilterDto negative = ProjectFilterDto.builder().build();
        assertTrue(projectNameFilter.isApplicable(positive));
        assertFalse(projectNameFilter.isApplicable(negative));
    }

    @Test
    public void testApply(){
        Project project1 = Project.builder().name("Test1").build();
        Project project2 = Project.builder().name("Test2").build();
        Project project3 = Project.builder().name("Test3").build();
        Project project4 = Project.builder().name("Name").build();
        ProjectFilterDto filter = ProjectFilterDto.builder().namePattern("Test").build();
        Stream<Project> result = projectNameFilter.apply(Stream.of(project1, project2, project3, project4), filter);
        Stream<Project> expected = Stream.of(project1, project2, project3);
        assertEquals(expected.toList(), result.toList());
    }
}
