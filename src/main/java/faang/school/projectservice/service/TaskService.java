package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.TaskGettingDto;
import faang.school.projectservice.dto.task.TaskResult;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.exception.OnePersonException;
import faang.school.projectservice.exception.ProjectWasNotFoundException;
import faang.school.projectservice.exception.UserIsNotInThatProjectException;
import faang.school.projectservice.exception.UserWasNotFoundException;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.filter.task.TaskGetting;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final StageRepository stageRepository;
    private final TaskMapper taskMapper;
    private final UserServiceClient userServiceClient;
    private final List<TaskGetting> filters;

    @Transactional
    public TaskResult createTask(CreateTaskDto createTaskDto) {
        Project project = findByProjectId(createTaskDto.projectId());
        Long taskCreatorId = createTaskDto.reporterUserId();
        areUsersInSystem(createTaskDto.performerUserId(), createTaskDto.reporterUserId());
        isItOnePerson(createTaskDto.performerUserId(), createTaskDto.reporterUserId());
        isUserInProject(project, taskCreatorId);

        Task parentTask = taskRepository.findById(createTaskDto.parentTaskId())
                .orElse(null);
        Stage stage = stageRepository.findById(createTaskDto.stageId())
                .orElse(null);
        List<Task> linkedTasks = createTaskDto.linkedTaskIds() != null
                ? createTaskDto.linkedTaskIds().stream().map(this::findTaskById).toList()
                : List.of();

        Task task = Task.builder()
                .name(createTaskDto.name())
                .description(createTaskDto.description())
                .status(createTaskDto.status() != null ? createTaskDto.status() : TaskStatus.TODO)
                .performerUserId(createTaskDto.performerUserId())
                .reporterUserId(createTaskDto.reporterUserId())
                .project(project)
                .parentTask(parentTask)
                .stage(stage)
                .linkedTasks(linkedTasks)
                .minutesTracked(createTaskDto.minutesTracked())
                .build();

        project.getTasks().add(task);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskResult updateTask(UpdateTaskDto updateTaskDto,
                                 Long taskId,
                                 Long userId) {
        Task task = findTaskById(taskId);
        isUserInProject(task.getProject(), userId);
        areUsersInSystem(userId);

        taskMapper.updateTaskFromDto(updateTaskDto, task);

        log.info("Task with id : {}, was updated by user with id: {}", taskId, userId);
        return taskMapper.toDto(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResult> getTasksFilter(@Valid TaskGettingDto taskGettingDto,
                                           Long userId,
                                           Long projectId) {
        Project project = findByProjectId(projectId);
        areUsersInSystem(userId);
        isUserInProject(project, userId);

        List<Task> tasks = project.getTasks();
        for (TaskGetting filter : filters) {
            if (filter.isApplicable(taskGettingDto)) {
                tasks = filter.filter(tasks.stream(), taskGettingDto)
                        .toList();
            }
        }

        return tasks
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResult> getProjectTasks(Long userId, Long projectId) {
        Project project = findByProjectId(projectId);
        areUsersInSystem(userId);
        isUserInProject(project, userId);
        return project.getTasks().stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResult findTaskByIdToDto(Long taskId) {
        return taskMapper.toDto(findTaskById(taskId));
    }

    public void isItOnePerson(Long performerUserId, Long reporterUserId) {
        if (performerUserId.equals(reporterUserId)) {
            log.error("PerformerUserId and reporterUserId is equals -> {}, {}",
                    performerUserId,
                    reporterUserId);
            throw new OnePersonException("PerformerUserId and reporterUserId is equals");
        }
    }

    public void areUsersInSystem(Long... userIds) {
        boolean isError = userServiceClient.getUsersByIds(Arrays.asList(userIds)).stream()
                .anyMatch(Objects::isNull);

        if (isError) {
            log.error("One or more users were not found -> userIds: {}", List.of(userIds));
            throw new UserWasNotFoundException("One or more users were not found in the database");
        }
    }

    public Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("task with was not found -> id : " + taskId));
    }

    public void isUserInProject(Project project, Long userId) {
        boolean isUserInProject = project.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers()
                        .stream().anyMatch(teamMember -> teamMember.getUserId().equals(userId)));
        if (!isUserInProject) {
            throw new UserIsNotInThatProjectException("User is not in that project -> user id : " + userId);
        }
    }

    public Project findByProjectId(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectWasNotFoundException("Project was not found with id : " + id));
    }
}
