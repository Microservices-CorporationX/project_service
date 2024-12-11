package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.stage.StageService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.validator.team_member.TeamMemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final TeamMemberValidator teamMemberValidator;
    private final StageService stageService;

    public void createTask(CreateTaskDto taskDto, long creatorId) {
        TeamMember taskCreator = teamMemberService.findById(creatorId);
        Project project = projectService.getProjectById(taskDto.getProjectId());
        teamMemberValidator.validateIsTeamMemberParticipantOfProject(taskCreator, project);

        Task task = taskMapper.toEntity(taskDto);
        task.setProject(project);
        setParentTaskIfIdNotNull(task, taskDto.getParentTaskId());
        setLinkedTasksIfListNotEmpty(task, taskDto.getLinkedTasksIds());
        setStageIfIdNotNull(task, taskDto.getStageId());

        taskRepository.save(task);
    }

    public void updateTask(UpdateTaskDto taskDto, long updaterId) {
        TeamMember taskCreator = teamMemberService.findById(updaterId);
        Project project = projectService.getProjectById(taskDto.getProjectId());
        teamMemberValidator.validateIsTeamMemberParticipantOfProject(taskCreator, project);

        Task task = taskMapper.toEntity(taskDto);
        task.setProject(project);
        setParentTaskIfIdNotNull(task, taskDto.getParentTaskId());
        setLinkedTasksIfListNotEmpty(task, taskDto.getLinkedTasksIds());

        taskRepository.save(task);
    }

    private Task findById(long id) {
        return taskRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException(
                        String.format("Task with Id: %d not found", id)));
    }

    private void setParentTaskIfIdNotNull(Task task, Long id) {
        if (id != null) task.setParentTask(findById(id));
    }

    private void setLinkedTasksIfListNotEmpty(Task task, List<Long> tasksIds) {
        if (!tasksIds.isEmpty()) {
            tasksIds.forEach(taskId ->
                    task.getLinkedTasks().add(findById(taskId)));
        }
    }

    private void setStageIfIdNotNull(Task task, Long id) {
        if (id != null) task.setStage(stageService.getStageEntity(id));
    }
}
