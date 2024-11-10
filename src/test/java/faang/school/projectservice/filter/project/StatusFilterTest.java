package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StatusFilterTest {
    private StatusFilter statusFilter = new StatusFilter();
    private ProjectFilterDto projectFilterDto = new ProjectFilterDto();

    @Test
    void testIsApplicableFalse() {
        assertFalse(statusFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testIsApplicableTrue() {
        projectFilterDto.setProjectStatus(ProjectStatus.CANCELLED);
        assertTrue(statusFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testApply() {
        Project firstProject = new Project();
        firstProject.setStatus(ProjectStatus.CANCELLED);
        Project secondProject = new Project();
        secondProject.setStatus(ProjectStatus.IN_PROGRESS);

        projectFilterDto.setProjectStatus(ProjectStatus.CANCELLED);

        List<Project> projects = new ArrayList<>();
        projects.add(firstProject);
        projects.add(secondProject);

        statusFilter.apply(projects, projectFilterDto);


        assertEquals(projects.get(0), firstProject);
        assertEquals(projects.size(), 1);

    }

}