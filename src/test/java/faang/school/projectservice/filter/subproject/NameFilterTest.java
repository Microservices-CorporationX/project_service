package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.project.FilterProjectDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NameFilterTest {
    private final NameFilter nameFilter = new NameFilter();

    @Test
    void testIsApplicableNotNullNotEmptyTrue() {
        FilterProjectDto filterDto = FilterProjectDto.builder().name("project").build();
        boolean result = nameFilter.isApplicable(filterDto);
        assertTrue(result);
    }

    @Test
    void testIsApplicableWithEmptyFalse() {
        FilterProjectDto filterDto = FilterProjectDto.builder().name("").build();
        boolean result = nameFilter.isApplicable(filterDto);
        assertFalse(result);
    }

    @Test
    void testIsApplicableWithNullFalse() {
        FilterProjectDto filterDto = FilterProjectDto.builder().build();
        boolean result = nameFilter.isApplicable(filterDto);
        assertFalse(result);
    }

    @Test
    void testApplySuccess() {
        FilterProjectDto filterDto = FilterProjectDto.builder().name("project").build();
        List<Project> projectStream = List.of(
                Project.builder().name("First").build(),
                Project.builder().name("Second project").build()
        );
        List<Project> projectsFiltered = List.of(Project.builder().name("Second project").build());

        Stream<Project> result = nameFilter.apply(projectStream.stream(), filterDto);
        assertEquals(result.toList(), projectsFiltered);
    }

    @Test
    void testApplyReturnFalseNullName() {
        FilterProjectDto filterDto = FilterProjectDto.builder().build();

        boolean result = nameFilter.isApplicable(filterDto);
        assertFalse(result);
    }

    @Test
    void testApplyReturnEmptyProjectNotMatch() {
        FilterProjectDto filterDto = FilterProjectDto.builder().name("Second project").build();
        List<Project> projectStream = List.of(
                Project.builder().name("First").build()
        );

        Stream<Project> result = nameFilter.apply(projectStream.stream(), filterDto);
        assertTrue(result.toList().isEmpty());
    }
}