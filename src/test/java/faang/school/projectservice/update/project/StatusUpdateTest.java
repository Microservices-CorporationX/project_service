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
public class StatusUpdateTest {
    StatusUpdate statusUpdate;

    ProjectDto projectDto;

    @BeforeEach
    public void setUp() {
        statusUpdate = new StatusUpdate();
        projectDto = new ProjectDto();
    }

    @Test
    public void testIsApplicableFailed() {
        projectDto.setStatus(null);

        boolean result = statusUpdate.isApplicable(projectDto);
        assertFalse(result);
    }

    @Test
    public void testIsApplicableSuccessful() {
        projectDto.setStatus("status");

        boolean result = statusUpdate.isApplicable(projectDto);
        assertTrue(result);
    }

    @Test
    public void testApplySuccessful() {
        Project project = new Project();
        projectDto.setStatus("CREATED");

        statusUpdate.apply(projectDto, project);
        assertEquals(projectDto.getStatus(), project.getStatus().name());
    }
}
