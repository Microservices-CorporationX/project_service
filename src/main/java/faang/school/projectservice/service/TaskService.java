package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.CreateTaskResult;
import faang.school.projectservice.exception.ProjectWasNotFoundException;
import faang.school.projectservice.exception.UserIsNotInThatProjectException;
import faang.school.projectservice.exception.UserWasNotFoundException;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;
    private final UserServiceClient userServiceClient;

    @Transactional
    public CreateTaskResult createTask(CreateTaskDto createTaskDto) {
        Project project = findByProjectId(createTaskDto.projectId());
        Long taskCreatorId = createTaskDto.reporterUserId();
        isThereThatUsersInSystem(createTaskDto.performerUserId(),
                createTaskDto.reporterUserId());
        isUserInProject(project, taskCreatorId);
        Task task = Task.builder()
                .name(createTaskDto.name())
                .performerUserId(createTaskDto.performerUserId())
                .reporterUserId(createTaskDto.performerUserId())
                .project(project)
                .build();

        project.getTasks().add(task);
        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    public void isThereThatUsersInSystem(Long performerId, Long reporterId) {
        boolean isError = userServiceClient.getUsersByIds(List.of(performerId, reporterId)).stream()
                .anyMatch(Objects::isNull);
        if(isError) {
            log.error("One of the users was not found -> performer: {}, reporter: {} ", performerId, reporterId);
            throw new UserWasNotFoundException("One of users was not found in database");
        }
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
