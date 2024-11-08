package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.project.FilterProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VisibilityFilterTest {
    private final VisibilityFilter visibilityFilter = new VisibilityFilter();

    @Test
    void testIsApplicableFilterNotNullAndPUBLIC() {
        FilterProjectDto filterDto = FilterProjectDto.builder().visibility(ProjectVisibility.PUBLIC).build();

        boolean result = visibilityFilter.isApplicable(filterDto);
        assertTrue(result);
    }

    @Test
    void testIsApplicableFilterNotNullAndPRIVATE() {
        FilterProjectDto filterDto = FilterProjectDto.builder().visibility(ProjectVisibility.PRIVATE).build();

        boolean result = visibilityFilter.isApplicable(filterDto);
        assertFalse(result);
    }

    @Test
    void testIsApplicableFilterNull() {
        FilterProjectDto filterDto = FilterProjectDto.builder().build();

        boolean result = visibilityFilter.isApplicable(filterDto);
        assertFalse(result);
    }


    @Test
    void testApplySuccess() {
        FilterProjectDto filterDto = FilterProjectDto.builder().visibility(ProjectVisibility.PUBLIC).build();
        List<Project> projectStream = List.of(
                Project.builder().visibility(ProjectVisibility.PUBLIC).build(),
                Project.builder().visibility(ProjectVisibility.PRIVATE).build()
        );
        List<Project> projectsFiltered = List.of(Project.builder().visibility(ProjectVisibility.PUBLIC).build());

        Stream<Project> result = visibilityFilter.apply(projectStream.stream(), filterDto);
        assertEquals(result.toList(), projectsFiltered);
    }

    @Test
    void testApplyReturnFalseWithFilterNullOrPRIVATE() {
        FilterProjectDto filterDtoNull = FilterProjectDto.builder().build();
        FilterProjectDto filterDtoPRIVATE = FilterProjectDto.builder().visibility(ProjectVisibility.PRIVATE).build();

        boolean resultNull = visibilityFilter.isApplicable(filterDtoNull);
        boolean resultPRIVATE = visibilityFilter.isApplicable(filterDtoPRIVATE);
        assertFalse(resultNull);
        assertFalse(resultPRIVATE);
    }


    @Test
    void testApplyReturnEmptyProjectNotMatch() {
        FilterProjectDto filterDto = FilterProjectDto.builder().visibility(ProjectVisibility.PUBLIC).build();
        List<Project> projectStream = List.of(
                Project.builder().visibility(ProjectVisibility.PRIVATE).build()
        );

        Stream<Project> result = visibilityFilter.apply(projectStream.stream(), filterDto);
        assertTrue(result.toList().isEmpty());
    }
}