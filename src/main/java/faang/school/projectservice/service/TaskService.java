package faang.school.projectservice.service;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.filter.task.TaskFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.validator.TaskValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskValidator taskValidator;
    private final List<TaskFilter> filters;
    private final ProjectService projectService;
    private final StageService stageService;

    @Transactional
    public TaskDto createTask(long userId, TaskDto taskDto) {
        log.info("User: {} is trying to create task: {}", userId, taskDto);
        Task task = mapToFullTask(userId, taskDto);
        taskValidator.validateCreateTask(task, userId);
        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskDto updateTask(long userId, long taskId, UpdateTaskDto updateTaskDto) {
        log.info("User: {} is trying to update task: {} with the following fields: {}",
                userId, taskId, updateTaskDto);
        Task task = getTaskById(taskId);
        validateUpdateTask(task, userId, updateTaskDto);
        updateTaskFromDto(task, updateTaskDto);
        return taskMapper.toDto(task);
    }

    @Transactional
    public List<TaskDto> getProjectTasks(long userId, long projectId, TaskFilterDto filterDto) {
        log.info("User: {} is trying to get project: {} tasks with the following filters: {}",
                userId, projectId, filterDto);
        Project project = projectService.findProjectById(projectId);
        taskValidator.validateProjectMembership(project, userId);
        List<Task> filteredTasks = applyFilters(project.getTasks(), filterDto);
        return taskMapper.toDto(filteredTasks);
    }

    @Transactional
    public TaskDto getTask(long userId, long taskId) {
        log.info("User: {} is trying to get task: {}", userId, taskId);
        Task task = getTaskById(taskId);
        Project project = task.getProject();
        taskValidator.validateProjectMembership(project, userId);
        return taskMapper.toDto(task);
    }

    private Task mapToFullTask(long userId, TaskDto taskDto) {
        Task task = taskMapper.toEntity(taskDto);

        Long stageId = taskDto.stageId();
        if (stageId != null) {
            task.setStage(stageService.getStage(stageId));
        }

        Long parentTaskId = taskDto.parentTaskId();
        if (parentTaskId != null) {
            task.setParentTask(getTaskById(parentTaskId));
        }

        task.setProject(projectService.findProjectById(taskDto.projectId()));
        task.setReporterUserId(userId);
        return task;
    }


    private void validateUpdateTask(Task task, long userId, UpdateTaskDto updateTaskDto) {
        Project project = task.getProject();

        taskValidator.validateProjectMembership(project, userId);
        taskValidator.validateParentTaskIsActive(task);

        Long parentTaskId = updateTaskDto.parentTaskId();
        if (parentTaskId != null) {
            taskValidator.validateParentTaskProjectMatches(getTaskById(updateTaskDto.parentTaskId()), task);
        }

        Long stageId = updateTaskDto.stageId();
        if (stageId != null) {
            taskValidator.validateStageProjectMatches(stageService.getStage(updateTaskDto.stageId()), task);
        }

        Long performerUserId = updateTaskDto.performerUserId();
        if (performerUserId != null) {
            taskValidator.validateProjectMembership(project, performerUserId);
        }
    }

    private void updateTaskFromDto(Task task, UpdateTaskDto updateTaskDto) {
        taskMapper.update(updateTaskDto, task);

        Long stageId = updateTaskDto.stageId();
        if (stageId != null) {
            task.setStage(stageService.getStage(stageId));
        }

        Long parentTaskId = updateTaskDto.parentTaskId();
        if (parentTaskId != null) {
            task.setParentTask(getTaskById(parentTaskId));
        }
    }

    private Task getTaskById(long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task does not exist"));
    }

    private List<Task> applyFilters(List<Task> tasks, TaskFilterDto filterDto) {
        List<TaskFilter> applicableFilters = filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .toList();

        for (TaskFilter filter : applicableFilters) {
            tasks = filter.apply(tasks, filterDto);
        }
        return tasks;
    }
}
