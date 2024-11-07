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
public class VisibilityUpdateTest {
    VisibilityUpdate visibilityUpdate;

    ProjectDto projectDto;

    @BeforeEach
    public void setUp() {
        visibilityUpdate = new VisibilityUpdate();
        projectDto = new ProjectDto();
    }

    @Test
    public void testIsApplicableFailed() {
        projectDto.setVisibility(null);

        boolean result = visibilityUpdate.isApplicable(projectDto);
        assertFalse(result);
    }

    @Test
    public void testIsApplicableSuccessful() {
        projectDto.setVisibility("visible");

        boolean result = visibilityUpdate.isApplicable(projectDto);
        assertTrue(result);
    }

    @Test
    public void testApplySuccessful() {
        Project project = new Project();
        projectDto.setVisibility("PUBLIC");

        visibilityUpdate.apply(projectDto, project);
        assertEquals(projectDto.getVisibility(), project.getVisibility().name());
    }
}
