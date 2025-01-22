package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ProjectVisibilityFilterTest {
    private ProjectFilterDto filterDto;
    private ProjectVisibilityFilter filter;
    private Project project1;
    private Project project2;
    private Project project3;

    @BeforeEach
    void setUp() {
        filterDto = new ProjectFilterDto();
        filter = new ProjectVisibilityFilter();
        project1 = new Project();
        project2 = new Project();
        project3 = new Project();
    }

    @Test
    void isApplicableTest_shouldReturnTrue_whenProjectVisibilityIsNotNull() {
        filterDto.setProjectVisibility(ProjectVisibility.PUBLIC);

        boolean result = filter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    void isApplicableTest_shouldReturnFalse_whenProjectVisibilityIsNull() {
        boolean result = filter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    void applyTest_shouldFilterProjectsByVisibility() {
        filterDto.setProjectVisibility(ProjectVisibility.PUBLIC);
        project1.setVisibility(ProjectVisibility.PUBLIC);
        project2.setVisibility(ProjectVisibility.PRIVATE);
        project3.setVisibility(ProjectVisibility.PUBLIC);
        List<Project> input = List.of(project1, project2, project3);
        List<Project> expected = List.of(project1, project3);

        List<Project> result = filter.apply(input.stream(), filterDto).toList();

        assertEquals(expected, result);
    }

    @Test
    void applyTest_shouldReturnEmpty_whenNoProjectsMatchVisibility() {
        filterDto.setProjectVisibility(ProjectVisibility.PUBLIC);
        project1.setVisibility(ProjectVisibility.PRIVATE);
        project2.setVisibility(ProjectVisibility.PRIVATE);
        List<Project> input = List.of(project1, project2);

        List<Project> result = filter.apply(input.stream(), filterDto).toList();

        assertEquals(List.of(), result);
    }
}