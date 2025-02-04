package school.faang.project_service.validator;

import static org.junit.jupiter.api.Assertions.*;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.validator.SubProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class SubProjectValidatorTest {

    private SubProjectValidator subProjectValidator;
    private Project parentProject;

    @BeforeEach
    public void setUp() {
        subProjectValidator = new SubProjectValidator();
        parentProject = new Project();
    }

    @Test
    public void testCanBeParentProject_ShouldThrowException_WhenInvalidParent() {
        parentProject.setParentProject(new Project());
        parentProject.setStatus(ProjectStatus.COMPLETED);

        assertThrows(IllegalArgumentException.class, () -> subProjectValidator.canBeParentProject(parentProject));
    }

    @Test
    void testCanBeParentProject_ShouldNotThrowException_WhenValidParent() {
        parentProject.setParentProject(null);
        parentProject.setStatus(ProjectStatus.CREATED);

        assertDoesNotThrow(() -> subProjectValidator.canBeParentProject(parentProject));
    }

    @Test
    public void testShouldBePublic_ShouldThrowException_WhenProjectIsPrivate() {
        parentProject.setVisibility(ProjectVisibility.PRIVATE);

        assertThrows(IllegalArgumentException.class, () -> subProjectValidator.shouldBePublic(parentProject));
    }

    @Test
    public void testShouldBePublic_ShouldNotThrowException_WhenProjectIsPublic() {
        parentProject.setVisibility(ProjectVisibility.PUBLIC);

        assertDoesNotThrow(() -> subProjectValidator.shouldBePublic(parentProject));
    }

    @Test
    public void testChildCompleted_ShouldThrowException_WhenChildNotCompletedOrCancelled() {
        Project childProject = new Project();
        childProject.setStatus(ProjectStatus.CREATED);
        List<Project> children = new ArrayList<>();
        children.add(childProject);

        assertThrows(IllegalArgumentException.class, () -> subProjectValidator.childCompleted(children));
    }

    @Test
    public void testChildCompleted_ShouldNotThrowException_WhenAllChildrenCompletedOrCancelled() {
        Project childProject1 = new Project();
        childProject1.setStatus(ProjectStatus.COMPLETED);

        Project childProject2 = new Project();
        childProject2.setStatus(ProjectStatus.CANCELLED);

        List<Project> children = List.of(childProject1, childProject2);

        assertDoesNotThrow(() -> subProjectValidator.childCompleted(children));
    }
}

