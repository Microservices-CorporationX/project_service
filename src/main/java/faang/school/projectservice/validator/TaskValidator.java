package faang.school.projectservice.validator;

import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskValidator {

    public void validateCreateTask(Task task, long userId) {
        validateProjectMembership(task.getProject(), userId);
        validateParentTaskIsActive(task);
        validateStageProjectMatches(task.getStage(), task);
        validateParentTaskProjectMatches(task.getParentTask(), task);
        validateLinkedTasksProjectsMatches(task.getLinkedTasks(), task);
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
        if (stage != null && !stage.getProject().equals(task.getProject())) {
            throw new DataValidationException("Stage and task must be from the same project");
        }
    }

    public void validateParentTaskProjectMatches(Task parentTask, Task task) {
        if (parentTask != null && !parentTask.getProject().equals(task.getProject())) {
            throw new DataValidationException("Child and parent tasks must be from the same project");
        }
    }

    public void validateLinkedTasksProjectsMatches(List<Task> linkedTasks, Task task) {
        if (linkedTasks != null) {
            boolean isAnyTaskFromOtherProject = linkedTasks.stream()
                    .anyMatch(linkedTask -> !linkedTask.getProject().equals(task.getProject()));

            if (isAnyTaskFromOtherProject) {
                throw new DataValidationException("Linked tasks must come from the same project as the task");
            }
        }
    }
}
