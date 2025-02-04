package school.faang.project_service.validator;

import static org.junit.jupiter.api.Assertions.*;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {

    private ProjectValidator projectValidator;
    private Project project;

    @BeforeEach
    public void setUp() {
        projectValidator = new ProjectValidator();
        project = new Project();
    }

    @Test
    public void testValidateIdNotNull_ShouldThrowException_WhenProjectIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> projectValidator.validateProjectIdNotNull(null));
    }

    @Test
    public void testValidateIdNotNull_ShouldNotThrowException_WhenProjectIdIsNotNull() {
        assertDoesNotThrow(() -> projectValidator.validateProjectIdNotNull(1L));
    }

    @Test
    public void testDoesProjectExist_ShouldThrowException_WhenProjectIsEmpty() {
        assertThrows(EntityNotFoundException.class, () -> projectValidator.doesProjectExist(Optional.empty()));
    }

    @Test
    public void testDoesProjectExist_ShouldNotThrowException_WhenProjectExists() {
        assertDoesNotThrow(() -> projectValidator.doesProjectExist(Optional.of(project)));
    }

    @Test
    public void testIsPublicProject_ShouldReturnTrue_WhenProjectIsPublic() {
        project.setVisibility(ProjectVisibility.PUBLIC);
        assertTrue(projectValidator.isPublicProject(project));
    }

    @Test
    public void testIsPublicProject_ShouldReturnFalse_WhenProjectIsNotPublic() {
        project.setVisibility(ProjectVisibility.PRIVATE);
        assertFalse(projectValidator.isPublicProject(project));
    }
}


