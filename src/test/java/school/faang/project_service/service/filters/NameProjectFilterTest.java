package school.faang.project_service.service.filters;

import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.filters.NameProjectFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NameProjectFilterTest {

    private NameProjectFilter nameFilter;
    private FilterSubProjectDto filterDto;
    private FilterSubProjectDto filterDtoFail;
    private Stream<Project> projects;

    @BeforeEach
    public void init() {
        nameFilter = new NameProjectFilter();
        filterDtoFail = new FilterSubProjectDto("", null, null);
        filterDto = new FilterSubProjectDto("Java", ProjectStatus.CREATED, ProjectVisibility.PUBLIC);
        projects = Stream.of(
                Project.builder().name("Learn Java coding").build(),
                Project.builder().name("Java interview session").build(),
                Project.builder().name("").build(),
                Project.builder().name(" ").build());
    }

    @Test
    public void testIsNotApplicable() {
        assertFalse(nameFilter.isApplicable(filterDtoFail));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(nameFilter.isApplicable(filterDto));
    }

    @Test
    public void testApplyFilter() {
        List<Project> result = nameFilter.apply(projects, filterDto).toList();

        assertEquals(2, result.size());
        result.forEach(project -> assertTrue(project.getName().contains("Java")));
    }
}


