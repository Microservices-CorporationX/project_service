package faang.school.projectservice.update.project;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class NameUpdateTest {
    NameUpdate nameUpdate;

    ProjectDto projectDto;

    @BeforeEach
    public void setUp() {
        nameUpdate = new NameUpdate();
        projectDto = new ProjectDto();
    }

    @Test
    public void testIsApplicableFailed() {
        projectDto.setName(null);

        boolean result = nameUpdate.isApplicable(projectDto);
        assertFalse(result);
    }

    @Test
    public void testIsApplicableSuccessful() {
        projectDto.setName("name");

        boolean result = nameUpdate.isApplicable(projectDto);
        assertTrue(result);
    }

    @Test
    public void testApplySuccessful() {
        Project project = new Project();
        projectDto.setName("name");

        nameUpdate.apply(projectDto, project);
        assertEquals(projectDto.getName(), project.getName());
    }
}
