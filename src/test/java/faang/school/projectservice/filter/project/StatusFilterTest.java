package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
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
public class StatusFilterTest {
    StatusFilter statusFilter;

    ProjectFilterDto projectFilterDto;

    @BeforeEach
    public void setUp() {
        statusFilter = new StatusFilter();
        projectFilterDto = new ProjectFilterDto();
    }

    @Test
    public void testIsApplicableFailed() {
        projectFilterDto.setStatus(null);
        boolean result = statusFilter.isApplicable(projectFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableSuccessful() {
        projectFilterDto.setStatus(ProjectStatus.CREATED);
        boolean result = statusFilter.isApplicable(projectFilterDto);

        assertTrue(result);
    }

    @Test
    public void testApplySuccessful() {
        Project firstProject = new Project();
        Project secondProject = new Project();
        firstProject.setStatus(ProjectStatus.CREATED);
        secondProject.setStatus(ProjectStatus.COMPLETED);
        projectFilterDto.setStatus(ProjectStatus.CREATED);
        Stream<Project> projects = Stream.of(firstProject, secondProject);

        List<Project> result = statusFilter.apply(projectFilterDto, projects).toList();

        assertEquals(1, result.size());
        assertEquals(ProjectStatus.CREATED, result.get(0).getStatus());
    }
}
