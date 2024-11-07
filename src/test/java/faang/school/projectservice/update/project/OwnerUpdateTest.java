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
public class OwnerUpdateTest {
    OwnerUpdate ownerUpdate;

    ProjectDto projectDto;

    @BeforeEach
    public void setUp() {
        ownerUpdate = new OwnerUpdate();
        projectDto = new ProjectDto();
    }

    @Test
    public void testIsApplicableFailed() {
        projectDto.setOwnerId(null);

        boolean result = ownerUpdate.isApplicable(projectDto);
        assertFalse(result);
    }

    @Test
    public void testIsApplicableSuccessful() {
        projectDto.setOwnerId(1L);

        boolean result = ownerUpdate.isApplicable(projectDto);
        assertTrue(result);
    }

    @Test
    public void testApplySuccessful() {
        Project project = new Project();
        projectDto.setOwnerId(1L);

        ownerUpdate.apply(projectDto, project);
        assertEquals(projectDto.getOwnerId(), project.getOwnerId());
    }
}
