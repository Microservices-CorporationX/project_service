package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
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
public class NameFilterTest {
    NameFilter nameFilter;

    ProjectFilterDto projectFilterDto;

    @BeforeEach
    public void setUp() {
        nameFilter = new NameFilter();
        projectFilterDto = new ProjectFilterDto();
    }

    @Test
    public void testIsApplicableFailed() {
        projectFilterDto.setName(null);
        boolean result = nameFilter.isApplicable(projectFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableSuccessful() {
        projectFilterDto.setName("name");
        boolean result = nameFilter.isApplicable(projectFilterDto);

        assertTrue(result);
    }

    @Test
    public void testApplySuccessful() {
        Project firstProject = new Project();
        Project secondProject = new Project();
        firstProject.setName("first");
        secondProject.setName("second");
        projectFilterDto.setName("first");
        Stream<Project> projects = Stream.of(firstProject, secondProject);

        List<Project> result = nameFilter.apply(projectFilterDto, projects).toList();

        assertEquals(1, result.size());
        assertEquals("first", result.get(0).getName());
    }
}
