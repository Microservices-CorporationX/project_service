package faang.school.projectservice.validator;

import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskValidator {

    public void validateCreateTask(Task task, long userId) {
        validateProjectMembership(task.getProject(), userId);
        validateParentTaskIsActive(task);
        validateStageProjectMatches(task.getStage(), task);
        validateParentTaskProjectMatches(task.getParentTask(), task);
    }

    public void validateProjectMembership(Project project, long userId) {
        if (project.hasNoTeamMember(userId)) {
            throw new AccessDeniedException("User is not participant of this project");
        }
    }

    public void validateParentTaskIsActive(Task task) {
        if (task.isParentTaskInactive()) {
            throw new DataValidationException("Parent task must be active");
        }
    }

    public void validateStageProjectMatches(Stage stage, Task task) {
        if (stage != null && stage.isProjectNotEqual(task.getProject())) {
            throw new DataValidationException("Stage and task must be from the same project");
        }
    }

    public void validateParentTaskProjectMatches(Task parentTask, Task task) {
        if (parentTask != null && parentTask.isProjectNotEqual(task.getProject())) {
            throw new DataValidationException("Child and parent tasks must be from the same project");
        }
    }
}
