package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.task.CreateUpdateTaskDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.Filter;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final TeamMemberValidator teamMemberValidator;
    private final StageService stageService;
    private final List<Filter<Task, TaskFilterDto>> filters;

    public void createTask(CreateUpdateTaskDto taskDto, long creatorId) {
        //create method for three rows below???
        //add logging

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

    public void updateTask(CreateUpdateTaskDto taskDto, long updaterId) {
        TeamMember taskUpdator = teamMemberService.findById(updaterId);
        Project project = projectService.getProjectById(taskDto.getProjectId());
        teamMemberValidator.validateIsTeamMemberParticipantOfProject(taskUpdator, project);

        Task task = taskMapper.toEntity(taskDto);
        task.setProject(project);
        setParentTaskIfIdNotNull(task, taskDto.getParentTaskId());
        setLinkedTasksIfListNotEmpty(task, taskDto.getLinkedTasksIds());

        taskRepository.save(task);
    }

    public TaskDto getTask(long taskId, long requesterId) {
        Task task = findById(taskId);
        TeamMember taskRequester = teamMemberService.findById(requesterId);
        Project project = projectService.getProjectById(task.getProject().getId());
        teamMemberValidator.validateIsTeamMemberParticipantOfProject(taskRequester, project);

        return taskMapper.toTaskDto(task);
    }

    public List<TaskDto> getAllTasks(TaskFilterDto filterDto, long requesterId, Long projectId) {
        TeamMember requester = teamMemberService.findById(requesterId);
        Project project = projectService.getProjectById(projectId);
        teamMemberValidator.validateIsTeamMemberParticipantOfProject(requester, project);

        List<Task> tasks = taskRepository.findAllByProjectId(projectId);
        return filterTasks(tasks.stream(), filterDto);
    }

    private List<TaskDto> filterTasks(Stream<Task> taskStream, TaskFilterDto taskFilter) {
        return filters.stream()
                .filter(filter -> filter.isApplicable(taskFilter))
                .reduce(taskStream,
                        (subStream, filter) -> filter.apply(subStream, taskFilter),
                        (a, b) -> b)
                .map(taskMapper::toTaskDto)
                .toList();
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
        List<Task> tasks = new ArrayList<>();
        if (!tasksIds.isEmpty()) {
            tasksIds.forEach(taskId -> tasks.add(findById(taskId)));
        }
        task.setLinkedTasks(tasks);
    }

    private void setStageIfIdNotNull(Task task, Long id) {
        if (id != null) task.setStage(stageService.getStageEntity(id));
    }
}
