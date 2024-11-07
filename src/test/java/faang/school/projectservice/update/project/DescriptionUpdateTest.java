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
public class DescriptionUpdateTest {
    DescriptionUpdate descriptionUpdate;

    ProjectDto projectDto;

    @BeforeEach
    public void setUp() {
        descriptionUpdate = new DescriptionUpdate();
        projectDto = new ProjectDto();
    }

    @Test
    public void testIsApplicableFailed() {
        projectDto.setDescription(null);

        boolean result = descriptionUpdate.isApplicable(projectDto);
        assertFalse(result);
    }

    @Test
    public void testIsApplicableSuccessful() {
        projectDto.setDescription("description");

        boolean result = descriptionUpdate.isApplicable(projectDto);
        assertTrue(result);
    }

    @Test
    public void testApplySuccessful() {
        Project project = new Project();
        projectDto.setDescription("description");

        descriptionUpdate.apply(projectDto, project);
        assertEquals(projectDto.getDescription(), project.getDescription());

    }
}
