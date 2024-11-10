package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class NameFilterTest {
    private NameFilter nameFilter = new NameFilter();
    private ProjectFilterDto projectFilterDto = new ProjectFilterDto();

    @Test
    void testIsApplicableFalse() {
        assertFalse(nameFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testIsApplicableTrue() {
        projectFilterDto.setName("Name");
        assertTrue(nameFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testApply() {
        Project firstProject = new Project();
        firstProject.setName("Name");
        Project secondProject = new Project();
        secondProject.setName("Not a name");

        projectFilterDto.setName("Name");

        List<Project> projects = new ArrayList<>();
        projects.add(firstProject);
        projects.add(secondProject);

        nameFilter.apply(projects, projectFilterDto);


        assertEquals(projects.get(0), firstProject);
        assertEquals(projects.size(), 1);

    }

}