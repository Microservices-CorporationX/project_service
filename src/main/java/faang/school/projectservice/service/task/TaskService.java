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
import faang.school.projectservice.validator.task.TaskValidator;
import faang.school.projectservice.validator.team_member.TeamMemberValidator;
import jakarta.transaction.Transactional;
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

    private final List<Filter<Task, TaskFilterDto>> filters;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskValidator taskValidator;
    private final StageService stageService;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final TeamMemberValidator teamMemberValidator;

    @Transactional
    public TaskDto createTask(CreateUpdateTaskDto taskDto, long creatorId) {
        taskValidator.validateTaskIdIsNull(taskDto.getId());
        TaskDto savedTask = processTask(taskDto, creatorId);
        log.info("Task created by team member with id: {}", creatorId);
        return savedTask;
    }

    @Transactional
    public TaskDto updateTask(CreateUpdateTaskDto taskDto, long updaterId) {
        taskValidator.validateTaskIdIsNotNull(taskDto.getId());
        TaskDto updatedTask = processTask(taskDto, updaterId);
        log.info("Task with id: {}, updated by team member with id: {}", taskDto.getId(), updaterId);
        return updatedTask;
    }

    @Transactional()
    public TaskDto getTask(long taskId, long requesterId) {
        Task task = findById(taskId);
        TeamMember taskRequester = teamMemberService.findById(requesterId);
        Project project = projectService.getProjectById(task.getProject().getId());
        teamMemberValidator.validateIsTeamMemberParticipantOfProject(taskRequester, project);

        log.info("Getting task with id: {}, requester id: {}", taskId, requesterId);
        return taskMapper.toTaskDto(task);
    }

    @Transactional
    public List<TaskDto> getAllTasks(TaskFilterDto filterDto, long requesterId, long projectId) {
        TeamMember requester = teamMemberService.findById(requesterId);
        Project project = projectService.getProjectById(projectId);
        teamMemberValidator.validateIsTeamMemberParticipantOfProject(requester, project);

        List<Task> tasks = taskRepository.findAllByProjectId(projectId);
        List<TaskDto> resultTasks = filterTasks(tasks.stream(), filterDto);
        log.info("Got {} tasks of project with id: {}, for team member with id: {}",
                resultTasks.size(), projectId, requesterId);
        return resultTasks;
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

    private TaskDto processTask(CreateUpdateTaskDto taskDto, long teamMemberId) {
        TeamMember taskCreator = teamMemberService.findById(teamMemberId);
        Project project = projectService.getProjectById(taskDto.getProjectId());
        teamMemberValidator.validateIsTeamMemberParticipantOfProject(taskCreator, project);

        Task task = taskMapper.toEntity(taskDto);
        task.setProject(project);
        setParentTaskIfIdNotNull(task, taskDto.getParentTaskId());
        setLinkedTasksIfListNotEmpty(task, taskDto.getLinkedTasksIds());
        setStageIfIdNotNull(task, taskDto.getStageId());

        return taskMapper.toTaskDto(taskRepository.save(task));
    }

    private Task findById(long id) {
        return taskRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException(
                        String.format("Task with Id: %d not found", id)));
    }

    private void setParentTaskIfIdNotNull(Task task, Long parentTaskId) {
        if (parentTaskId != null) {
            task.setParentTask(findById(parentTaskId));
        }
    }

    private void setLinkedTasksIfListNotEmpty(Task task, List<Long> tasksIds) {
        List<Task> tasks = new ArrayList<>();
        if (!tasksIds.isEmpty()) {
            tasksIds.forEach(taskId -> tasks.add(findById(taskId)));
        }
        task.setLinkedTasks(tasks);
    }

    private void setStageIfIdNotNull(Task task, Long stageId) {
        if (stageId != null) {
            task.setStage(stageService.getStageEntity(stageId));
        }
    }
}