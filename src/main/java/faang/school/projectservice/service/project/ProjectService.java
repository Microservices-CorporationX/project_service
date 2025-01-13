package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ChangeTaskStatusDto;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.publisher.TaskCompletedEvent.TaskCompletedEvent;
import faang.school.projectservice.publisher.TaskCompletedEvent.TaskCompletedEventPublisher;
import faang.school.projectservice.publisher.projectview.ProjectViewEvent;
import faang.school.projectservice.publisher.projectview.ProjectViewEventPublisher;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private static final String PROJECT_EXISTS_ERROR = "Project with the same name already exists.";
    private static final String PROJECT_NOT_FOUND = "Project with id %d is not found";
    private static final String UNAUTHORIZED_TASK_CHANGE = "User %d is not authorized to modify task %d";

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserContext userContext;
    private final List<ProjectFilter> filtersForProjects;
    private final ProjectViewEventPublisher projectViewEventPublisher;
    private final TaskCompletedEventPublisher taskCompletedEventPublisher;

    private static final ProjectStatus projectDefaultStatus = ProjectStatus.CREATED;
    private static final ProjectVisibility projectDefaultVisibility = ProjectVisibility.PUBLIC;

    public ProjectResponseDto createProject(ProjectCreateDto projectCreateDto) {
        Long currentUserId = userContext.getUserId();

        if (projectRepository.existsByOwnerUserIdAndName(currentUserId, projectCreateDto.getName())) {
            throw new IllegalArgumentException(PROJECT_EXISTS_ERROR);
        }

        Project project = projectMapper.toEntityFromCreateDto(projectCreateDto);

        project.setOwnerId(currentUserId);
        project.setStatus(projectDefaultStatus);
        project.setVisibility(projectDefaultVisibility);

        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponseDtoFromEntity(savedProject);
    }

    public ProjectResponseDto updateProject(Long projectId, ProjectUpdateDto projectUpdateDto) {
        Project projectForUpdate = projectRepository.getProjectById(projectId);

        if (projectUpdateDto.getVisibility() != null) {
            projectForUpdate.setVisibility(projectUpdateDto.getVisibility());
        }
        projectForUpdate.setDescription(projectUpdateDto.getDescription());
        projectForUpdate.setStatus(projectUpdateDto.getStatus());
        projectForUpdate.setUpdatedAt(LocalDateTime.now());

        Project saveProject = projectRepository.save(projectForUpdate);
        return projectMapper.toResponseDtoFromEntity(saveProject);
    }

    public List<ProjectResponseDto> findAllProjectsWithFilters(ProjectFilterDto filterDto) {
        Stream<Project> projectStream = projectRepository.findAll().stream();

        Stream<ProjectResponseDto> projectResponseDtoStream = filtersForProjects.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(projectStream, (streamProject, filter) -> filter.apply(streamProject, filterDto), (s1, s2) -> s1)
                .map(projectMapper::toResponseDtoFromEntity);
        return projectResponseDtoStream.toList();
    }

    public ProjectResponseDto getProjectById(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException(PROJECT_NOT_FOUND.formatted(projectId));
        }
        Project projectById = projectRepository.getProjectById(projectId);
        return projectMapper.toResponseDtoFromEntity(projectById);
    }

    public List<ProjectResponseDto> findAllProject() {
        List<Project> allProjects = projectRepository.findAll();
        return allProjects.stream().map(projectMapper::toResponseDtoFromEntity).toList();
    }

    public Project findProjectById(@Positive Long id) {
        return projectRepository.findProjectById(id);
    }

    public void saveProject(Project project) {
        projectRepository.save(project);
    }

    public ProjectResponseDto viewProject(Long projectId, Long userId) {
        log.info("Starting viewProject method. Project ID: {}, User ID: {}", projectId, userId);

        Project project = projectRepository.getProjectById(projectId);
        log.info("Project retrieved successfully. Project ID: {}, Owner ID: {}", projectId, project.getOwnerId());

        if (!userId.equals(project.getOwnerId())) {
            log.info("User ID {} is viewing a project they do not own. Preparing ProjectViewEvent.", userId);
            ProjectViewEvent event = ProjectViewEvent.builder()
                    .projectId(projectId)
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .build();
            projectViewEventPublisher.publish(event);
        } else {
            log.warn("User ID {} is the owner of Project ID {}. No event will be published.", userId, projectId);
        }

        ProjectResponseDto responseDto = projectMapper.toResponseDtoFromEntity(project);
        log.info("ProjectResponseDto successfully created for Project ID: {}", projectId);

        log.info("viewProject method execution completed. Project ID: {}, User ID: {}", projectId, userId);
        return responseDto;
    }

    @Transactional
    public ChangeTaskStatusDto changeTaskStatus(ChangeTaskStatusDto changeStatusDto, long executorId) {
        Project project = findProjectById(changeStatusDto.getProjectId());
        Task task = findAndValidateTask(project, changeStatusDto.getTaskId(), executorId);

        updateTaskStatus(task, changeStatusDto.getTaskStatus());

        TaskCompletedEvent event = createTaskCompletedEvent(task, project.getId());
        publishTaskStatusChange(event);

        AtomicReference<ChangeTaskStatusDto> eventDto = new AtomicReference<>(ChangeTaskStatusDto.builder()
                .projectId(project.getId())
                .taskId(task.getId())
                .taskStatus(task.getStatus())
                .build());

        return eventDto.get();
    }

    private Task findAndValidateTask(Project project, Long taskId, Long executorId) {
        return project.getTasks().stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .map(task -> validateTaskExecutor(task, executorId))
                .orElseThrow(() -> new DataValidationException("Task not found"));
    }

    private Task validateTaskExecutor(Task task, Long executorId) {
        if (!task.getPerformerUserId().equals(executorId)) {
            throw new DataValidationException(
                    String.format(UNAUTHORIZED_TASK_CHANGE, executorId, task.getId()));
        }
        return task;
    }

    private void updateTaskStatus(Task task, TaskStatus newStatus) {
        task.setStatus(newStatus);
        log.info("Task {} status updated to {}", task.getId(), newStatus);
    }

    private TaskCompletedEvent createTaskCompletedEvent(Task task, Long projectId) {
        if (task.getStatus() != TaskStatus.DONE) {
            return null;
        }
        return TaskCompletedEvent.builder()
                .projectId(projectId)
                .taskId(task.getId())
                .userId(task.getPerformerUserId())
                .taskStatus(task.getStatus())
                .build();
    }

    private void publishTaskStatusChange(TaskCompletedEvent event) {
        if (event != null) {
            taskCompletedEventPublisher.publish(event);
            log.info("Published task completion event for task {}", event.getTaskId());
        }
    }
}