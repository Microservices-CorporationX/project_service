package faang.school.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class TaskValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectService projectService;
    private final StageService stageService;
    private final TaskRepository taskRepository;

    public void validateString(String text) {
        if (text == null || text.isBlank()) {
            throw new DataValidationException("Task text should not be empty");
        }
    }

    public void validateStatus(TaskStatus status) {
        if (status == null || status.name().isBlank()) {
            throw new DataValidationException("Task status should not be empty");
        }

        try {
            TaskStatus.valueOf(String.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new DataValidationException("Task status is not valid. Validation status: " + Arrays.toString(TaskStatus.values()));
        }
    }

    public void validateUser(Long userId) {
        if (userId == null) {
            throw new DataValidationException("User id should not be empty");
        }

        if (userServiceClient.getUser(userId) == null) {
            throw new DataValidationException("User with id " + userId + " does not exist");
        }
    }

    public void validateTask(Long taskId) {
        if (taskId != null && taskRepository.findById(taskId).isEmpty()) {
            throw new DataValidationException("Task with id " + taskId + " does not exist");
        }
    }

    public void validateProject(Long projectId) {
        if (projectId == null) {
            throw new DataValidationException("Project id should not be empty");
        }

        if (projectService.getProjectById(projectId) == null) {
            throw new DataValidationException("Project with id " + projectId + " does not exist");
        }
    }

    public void validateStage(Long stageId) {
        if (stageId == null) {
            throw new DataValidationException("Stage id should not be empty");
        }

        if (stageService.getById(stageId) == null) {
            throw new DataValidationException("Stage with id " + stageId + " does not exist");
        }
    }

    public void validateTeamMember(Long teamMemberId, Long projectId) {
        Project project = projectService.getProjectById(projectId);
        AtomicBoolean isFounded = new AtomicBoolean(false);

        project.getTeams().forEach(team -> {
            team.getTeamMembers().forEach(teamMember -> {
                if (teamMember.getId().equals(teamMemberId)) {
                    isFounded.set(true);
                }
            });
        });

        if (!isFounded.get()) {
            throw new DataValidationException("Team member with id " + teamMemberId + " does not exist in project with id " + projectId);
        }
    }
}
