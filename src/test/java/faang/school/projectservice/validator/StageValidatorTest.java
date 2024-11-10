package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StageValidatorTest {
    private final StageValidator stageValidator = new StageValidator();
    private final String invalidRole = "role";
    private final String invalidStatus = "status";
    private final String correctRole = "developer";
    private final String correctStatus = "in_progress";

    @Test
    void validateStageRoleShouldThrowExceptionWhenRoleIsNull() {
        Exception exception = assertThrows(DataValidationException.class, () -> stageValidator.validateStageRole(null));

        assertEquals("TeamRole cannot be null", exception.getMessage());
    }

    @Test
    void validateStageRoleShouldThrowExceptionWhenRoleIsInvalid() {
        Exception exception = assertThrows(DataValidationException.class, () -> stageValidator.validateStageRole(invalidRole));

        assertEquals("Invalid TeamRole: " + invalidRole, exception.getMessage());
    }

    @Test
    void validateStageRoleShouldNotThrowExceptionWhenRoleIsValid() {
        assertDoesNotThrow(() -> stageValidator.validateStageRole(correctRole));
    }

    @Test
    void validateTaskStatusShouldThrowExceptionWhenStatusIsNull() {
        Exception exception = assertThrows(DataValidationException.class, () -> stageValidator.validateTaskStatus(null));

        assertEquals("Status cannot be null", exception.getMessage());
    }

    @Test
    void validateTaskStatusShouldThrowExceptionWhenStatusIsInvalid() {
        Exception exception = assertThrows(DataValidationException.class, () -> stageValidator.validateTaskStatus(invalidStatus));

        assertEquals("Invalid TaskStatus: " + invalidStatus, exception.getMessage());
    }

    @Test
    void validateTaskStatusShouldNotThrowExceptionWhenStatusIsValid() {
        assertDoesNotThrow(() -> stageValidator.validateTaskStatus(correctStatus));
    }
}
