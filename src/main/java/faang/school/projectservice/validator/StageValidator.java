package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class StageValidator {

    public void validateStageRole(String role) {
        if (role == null) {
            throw new DataValidationException("TeamRole cannot be null");
        }
        TeamRole.getAll().stream()
                .filter(teamRole -> Objects.equals(teamRole.toString(), role.toLowerCase()))
                .findAny()
                .orElseThrow(() -> new DataValidationException("Invalid TeamRole: " + role));
    }

    public void validateTaskStatus(String status) {
        if (status == null) {
            throw new DataValidationException("Status cannot be null");
        }
        TaskStatus.getAll().stream()
                .filter(taskStatus -> Objects.equals(taskStatus.toString(), status.toLowerCase()))
                .findAny()
                .orElseThrow(() -> new DataValidationException("Invalid TaskStatus: " + status));
    }
}
