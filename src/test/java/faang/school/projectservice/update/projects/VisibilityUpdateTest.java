package faang.school.projectservice.update.projects;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


class VisibilityUpdateTest {
    private VisibilityUpdate statusUpdate = new VisibilityUpdate();
    private ProjectDto projectDto = Mockito.mock(ProjectDto.class);
    private Project project = Mockito.mock(Project.class);
    private Project parentProject = Mockito.mock(Project.class);

    @Test
    void testIsApplicableFalse() {
        assertFalse(statusUpdate.isApplicable(projectDto));
    }

    @Test
    void testIsApplicableTrue() {
        Mockito.when(projectDto.getVisibility()).thenReturn(ProjectVisibility.PUBLIC);
        assertTrue(statusUpdate.isApplicable(projectDto));
    }

    @Test
    void testApplyWhenParentVisibilityIsNotNullValidatesVisibility() {
        Mockito.when(project.getParentProject()).thenReturn(parentProject);
        Mockito.when(parentProject.getVisibility()).thenReturn(ProjectVisibility.PUBLIC);
        Mockito.when(projectDto.getVisibility()).thenReturn(ProjectVisibility.PRIVATE);

        assertThrows(IllegalArgumentException.class,
                () -> statusUpdate.apply(project, projectDto));
    }

    @Test
    void testApplySuccess() {
        Mockito.when(project.getParentProject()).thenReturn(parentProject);
        Mockito.when(parentProject.getVisibility()).thenReturn(ProjectVisibility.PRIVATE);
        Mockito.when(projectDto.getVisibility()).thenReturn(ProjectVisibility.PRIVATE);
        Mockito.when(project.getChildren()).thenReturn(List.of(new Project(), new Project()));

        statusUpdate.apply(project, projectDto);
        Mockito.verify(project).setVisibility(ProjectVisibility.PRIVATE);

        assertEquals(project.getChildren().size(), 2);
        assertEquals(project.getChildren().get(0).getVisibility(), ProjectVisibility.PRIVATE);
        assertEquals(project.getChildren().get(1).getVisibility(), ProjectVisibility.PRIVATE);
    }


}