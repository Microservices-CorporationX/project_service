package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.task.TaskDTO;
import faang.school.projectservice.dto.task.TaskFilterDTO;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.task.filter.TaskFilter;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TaskMapper taskMapper;
    private final List<TaskFilter> taskFilters;


    public TaskDTO createTask(TaskDTO taskDTO) {
        log.info("Создание задачи: {}", taskDTO.getName());

        Project project = projectRepository.getProjectById(taskDTO.getProjectId());
        TeamMember reporter = teamMemberRepository.findById(taskDTO.getReporterUserId());
        TeamMember performer = teamMemberRepository.findById(taskDTO.getPerformerUserId());

        Task task = taskMapper.toEntity(taskDTO);
        task.setProject(project);
        task.setReporterUserId(reporter.getId());
        task.setPerformerUserId(performer.getId());

        Task savedTask = taskRepository.save(task);
        log.info("Задача успешно создана с ID: {}", savedTask.getId());
        return taskMapper.toDto(savedTask);
    }

    public TaskDTO updateTask(Long taskId, TaskDTO taskDTO) {
        log.info("Обновление задачи с ID: {}", taskId);

        Task existingTask = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Задача с таким ID не найдена"));

        BeanUtils.copyProperties(taskDTO, existingTask, getNullPropertyNames(taskDTO));


        Task updatedTask = taskRepository.save(existingTask);
        log.info("Задача с ID: {} успешно обновлена", updatedTask.getId());

        return taskMapper.toDto(updatedTask);
    }

    private String[] getNullPropertyNames(TaskDTO taskDTO) {
        final var wrapper = new BeanWrapperImpl(taskDTO);
        return Arrays.stream(wrapper.getPropertyDescriptors())
            .filter(pd -> wrapper.getPropertyValue(pd.getName()) == null)
            .map(pd -> pd.getName())
            .toArray(String[]::new);
    }

    public List<TaskDTO> getFilteredTasks(TaskFilterDTO taskFilterDTO) {
        log.info("Запрос на фильтрацию задач с фильтром: {}", taskFilterDTO);

        if (taskFilterDTO == null) {
            log.warn("Фильтр не был передан");
            throw new ValidationException("Фильтр не может быть null");
        }

        List<Task> tasks = taskRepository.findAllByProjectId(taskFilterDTO.getProjectId());
        if (tasks == null || tasks.isEmpty()) {
            log.warn("Задачи не найдены для проекта с ID: {}", taskFilterDTO.getProjectId());
            return Collections.emptyList();
        }

        return taskFilters.stream()
            .filter(filter -> filter.isApplicable(taskFilterDTO))
            .flatMap(filter -> filter.apply(tasks.stream(), taskFilterDTO))
            .map(taskMapper::toDto)
            .collect(Collectors.toList());
    }

    public TaskDTO getTaskById(Long taskId) {
        log.info("Получение задачи с ID: {}", taskId);

        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Задача с таким ID не найдена"));

        return taskMapper.toDto(task);
    }

    public List<TaskDTO> getAllTasksByProjectId(Long projectId) {
        log.info("Запрос на получение всех задач проекта с ID: {}", projectId);
        List<Task> tasks = taskRepository.findAllByProjectId(projectId);
        if (tasks == null || tasks.isEmpty()) {
            log.warn("Задачи не найдены для проекта с ID: {}", projectId);
            return Collections.emptyList();
        }
        return tasks.stream()
            .map(taskMapper::toDto)
            .collect(Collectors.toList());
    }
}
