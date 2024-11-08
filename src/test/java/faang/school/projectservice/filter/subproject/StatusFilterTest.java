package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.project.FilterProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StatusFilterTest {
    private final StatusFilter statusFilter = new StatusFilter();

    @Test
    void testIsApplicableFilterNotNull() {
        FilterProjectDto filterDto = FilterProjectDto.builder().status(ProjectStatus.COMPLETED).build();

        boolean result = statusFilter.isApplicable(filterDto);
        assertTrue(result);
    }

    @Test
    void testIsApplicableFilterNull() {
        FilterProjectDto filterDto = FilterProjectDto.builder().build();

        boolean result = statusFilter.isApplicable(filterDto);
        assertFalse(result);
    }

    @Test
    void testApplySuccess() {
        FilterProjectDto filterDto = FilterProjectDto.builder().status(ProjectStatus.COMPLETED).build();
        List<Project> projectStream = List.of(
                Project.builder().status(ProjectStatus.COMPLETED).build(),
                Project.builder().status(ProjectStatus.IN_PROGRESS).build()
        );
        List<Project> projectsFiltered = List.of(Project.builder().status(ProjectStatus.COMPLETED).build());

        Stream<Project> result = statusFilter.apply(projectStream.stream(), filterDto);
        assertEquals(result.toList(), projectsFiltered);
    }

    @Test
    void testApplyReturnFalseWithFilterNull() {
        FilterProjectDto filterDto = FilterProjectDto.builder().build();

        boolean result = statusFilter.isApplicable(filterDto);
        assertFalse(result);
    }

    @Test
    void testApplyReturnEmptyProjectNotMatch() {
        FilterProjectDto filterDto = FilterProjectDto.builder().status(ProjectStatus.COMPLETED).build();
        List<Project> projectStream = List.of(
                Project.builder().status(ProjectStatus.IN_PROGRESS).build()
        );

        Stream<Project> result = statusFilter.apply(projectStream.stream(), filterDto);
        assertTrue(result.toList().isEmpty());
    }
}