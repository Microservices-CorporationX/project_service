package faang.school.projectservice.service;


import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.ResponseTaskDto;
import faang.school.projectservice.dto.task.TaskFiltersDto;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.validator.TaskValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskValidator taskValidator;
    private final TaskMapper taskMapper;
    private final ProjectService projectService;
    private final StageService stageService;

    private final List<Filter<Task, TaskFiltersDto>> taskFilters;

    private static final int MAX_TASKS_PER_PAGE = 100;
    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LIMIT = 10;

    public ResponseTaskDto create(CreateTaskDto createTaskDto) {
        taskValidator.validateString(createTaskDto.getName());
        taskValidator.validateString(createTaskDto.getDescription());
        taskValidator.validateStatus(createTaskDto.getStatus());
        taskValidator.validateUser(createTaskDto.getPerformerUserId());
        taskValidator.validateUser(createTaskDto.getReporterUserId());
        taskValidator.validateTask(createTaskDto.getParentTaskId());
        taskValidator.validateProject(createTaskDto.getProjectId());
        taskValidator.validateStage(createTaskDto.getStageId());
        taskValidator.validateTeamMember(createTaskDto.getPerformerUserId(), createTaskDto.getProjectId());

        Task task = taskMapper.toEntity(createTaskDto);
        task.setProject(projectService.getProjectById(createTaskDto.getProjectId()));
        task.setStage(stageService.getById(createTaskDto.getStageId()));
        if (createTaskDto.getParentTaskId() != null) {
            task.setParentTask(taskRepository.findById(createTaskDto.getParentTaskId()).orElse(null));
        }
        task.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC+3")));

        log.info("Task {} created successfully", task.getId());

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public ResponseTaskDto update(UpdateTaskDto updateTaskDto) {
        taskValidator.validateTask(updateTaskDto.getId());
        taskValidator.validateString(updateTaskDto.getDescription());
        taskValidator.validateStatus(updateTaskDto.getStatus());
        taskValidator.validateUser(updateTaskDto.getPerformerUserId());
        taskValidator.validateTask(updateTaskDto.getParentTaskId());

        Task task = taskRepository.findById(updateTaskDto.getId()).get();

        taskValidator.validateTeamMember(updateTaskDto.getPerformerUserId(), task.getProject().getId());

        task.setDescription(updateTaskDto.getDescription());
        task.setStatus(updateTaskDto.getStatus());
        task.setPerformerUserId(updateTaskDto.getPerformerUserId());
        task.setProject(projectService.getProjectById(task.getProject().getId()));
        task.setStage(stageService.getById(task.getStage().getStageId()));

        if (updateTaskDto.getParentTaskId() != null) {
            task.setParentTask(taskRepository.findById(updateTaskDto.getParentTaskId()).orElse(null));
        }

        task.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC+3")));

        log.info("Task {} updated successfully", task.getId());

        return taskMapper.toDto(taskRepository.save(task));
    }

    public List<ResponseTaskDto> filterTasks(TaskFiltersDto filters, Long userId, Long projectId) {
        taskValidator.validateUser(userId);
        taskValidator.validateProject(projectId);
        taskValidator.validateTeamMember(userId, projectId);

        Stream<Task> tasks = taskRepository.findTasksByProjectId(projectId).stream();

        Stream<Task> filteredGoals = taskFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(
                        tasks,
                        (currentStream, filter) -> filter.apply(currentStream, filters),
                        (s1, s2) -> s1
                );

        log.info("Tasks filtered by {}.", filters);

        return filteredGoals.map(taskMapper::toDto).toList();
    }

    public List<ResponseTaskDto> getTasks(Long userId, Long projectId, Integer limit, Integer offset) {
        taskValidator.validateUser(userId);
        taskValidator.validateProject(projectId);
        taskValidator.validateTeamMember(userId, projectId);

        limit = getLimit(limit);
        offset = getOffset(offset);

        Pageable pageable = PageRequest.of(offset, limit);
        Stream<Task> tasks = taskRepository.findTasksByProjectId(projectId, pageable).stream();

        log.info("Tasks by project {}.", projectId);

        return tasks.map(taskMapper::toDto).toList();
    }

    public ResponseTaskDto getTaskById(Long userId, Long projectId, Long taskId) {
        taskValidator.validateUser(userId);
        taskValidator.validateProject(projectId);
        taskValidator.validateTask(taskId);
        taskValidator.validateTeamMember(userId, projectId);

        Task task = taskRepository.findById(taskId).get();

        log.info("Task by id {}.", taskId);

        return taskMapper.toDto(task);
    }

    private int getLimit(int limit) {
        return (limit != 0 && limit <= MAX_TASKS_PER_PAGE) ? limit : DEFAULT_LIMIT;
    }

    private int getOffset(int offset) {
        return (offset != 0) ? offset : DEFAULT_OFFSET;
    }
}
