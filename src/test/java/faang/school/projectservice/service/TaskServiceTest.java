package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.TaskGettingDto;
import faang.school.projectservice.dto.task.TaskResult;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.exception.OnePersonException;
import faang.school.projectservice.exception.UserIsNotInThatProjectException;
import faang.school.projectservice.exception.UserWasNotFoundException;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.command.TaskUpdateRegistry;
import faang.school.projectservice.service.filter.task.NameFilter;
import faang.school.projectservice.service.filter.task.StatusFilter;
import faang.school.projectservice.service.filter.task.TaskGetting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserServiceClient client;
    @Mock
    private TaskUpdateRegistry taskUpdateRegistry;
    @Spy
    private List<TaskGetting> filters = new ArrayList<>();
    @Spy
    private TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);

    private static CreateTaskDto createTaskDto;
    private static TaskResult taskResult;
    private static Task task;
    private static Project project;
    private static UserDto userDto1;
    private static UserDto userDto2;

    @BeforeEach
    public void catchUp() {
        createTaskDto = CreateTaskDto.builder()
                .name("Norair")
                .performerUserId(1L)
                .reporterUserId(2L)
                .projectId(1L)
                .build();
        taskResult = TaskResult.builder()
                .id(1L)
                .build();
        project = Project.builder()
                .id(1L)
                .tasks(List.of())
                .name("task1")
                .description("idk what to write!")
                .tasks(new ArrayList<>())
                .teams(new ArrayList<>())
                .build();
        task = Task.builder()
                .id(1L)
                .description("Hello world")
                .project(project)
                .build();
        userDto1 = UserDto.builder()
                .id(1L)
                .email("createdMock@mail.ru")
                .username("az3l1t")
                .build();
        userDto2 = UserDto.builder()
                .id(2L)
                .email("createdMock@mail.ru")
                .username("az3l1t")
                .build();
    }

    @Test
    public void createTask_SuccessfullyCreatedTask() {
        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .userId(userDto2.id())
                .build();
        Team team = Team.builder()
                .project(project)
                .teamMembers(new ArrayList<>(List.of(teamMember)))
                .id(1L)
                .build();
        project.getTeams().add(team);
        project.getTeams().get(0).getTeamMembers().add(teamMember);

        Mockito.when(projectRepository.findById(project.getId())).thenReturn(Optional.ofNullable(project));
        Mockito.when(client.getUsersByIds(List.of(createTaskDto.performerUserId(), createTaskDto.reporterUserId())))
                .thenReturn(List.of(userDto1, userDto2));
        Mockito.when(taskRepository.save(Mockito.any(Task.class))).thenReturn(task);

        TaskResult providedResult = taskService.createTask(createTaskDto);
        Assertions.assertEquals(providedResult, taskResult);
    }

    @Test
    public void createTask_UserIsNotInSystem() {
        UserDto userDtoNullable = null;

        Mockito.when(projectRepository.findById(project.getId())).thenReturn(Optional.ofNullable(project));
        Mockito.when(client.getUsersByIds(Mockito.anyList()))
                .thenReturn(new ArrayList<>(Arrays.asList(userDto1, userDtoNullable)));

        Assertions.assertThrows(UserWasNotFoundException.class, () -> taskService.createTask(createTaskDto));
    }

    @Test
    public void createTask_ItIsOnePerson() {
        UserDto userDtoTest = UserDto.builder()
                .id(1L)
                .build();

        CreateTaskDto createTaskDtoTest = CreateTaskDto.builder()
                .projectId(1L)
                .reporterUserId(userDtoTest.id())
                .performerUserId(userDtoTest.id())
                .build();

        Mockito.when(projectRepository.findById(project.getId())).thenReturn(Optional.ofNullable(project));
        Mockito.when(client.getUsersByIds(Mockito.anyList()))
                .thenReturn(new ArrayList<>(Arrays.asList(userDto1, userDto2)));

        Assertions.assertThrows(OnePersonException.class, () -> taskService.createTask(createTaskDtoTest));
    }

    @Test
    public void createTask_UserIsNotInProject() {
        Mockito.when(projectRepository.findById(project.getId())).thenReturn(Optional.ofNullable(project));
        Mockito.when(client.getUsersByIds(List.of(createTaskDto.performerUserId(), createTaskDto.reporterUserId())))
                .thenReturn(List.of(userDto1, userDto2));

        Assertions.assertThrows(UserIsNotInThatProjectException.class, () -> taskService.createTask(createTaskDto));
    }

    @Test
    public void updateTask_SuccessfullyUpdatingTask() {
        Long userId = userDto1.id();
        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .userId(userId)
                .build();
        Team team = Team.builder()
                .project(project)
                .teamMembers(new ArrayList<>(List.of(teamMember)))
                .id(1L)
                .build();
        UpdateTaskDto updateTaskDto = UpdateTaskDto.builder()
                .description("oops")
                .status(TaskStatus.TESTING)
                .build();

        project.getTeams().add(team);
        Mockito.when(taskRepository.findById(task.getId()))
                .thenReturn(Optional.ofNullable(task));
        Mockito.when(client.getUsersByIds(Mockito.anyList())).thenReturn(new ArrayList<>(List.of(userDto1)));


        TaskResult providedResult = taskService.updateTask(updateTaskDto, task.getId(), userId);
        Assertions.assertEquals(providedResult, taskResult);
    }

    @Test
    public void updateTask_UserIsNotInProject() {
        Long userId = userDto1.id();
        UpdateTaskDto updateTaskDto = UpdateTaskDto.builder()
                .description("oops")
                .status(TaskStatus.TESTING)
                .build();

        Mockito.when(taskRepository.findById(task.getId()))
                .thenReturn(Optional.ofNullable(task));

        Assertions.assertThrows(UserIsNotInThatProjectException.class, () ->
                taskService.updateTask(updateTaskDto, task.getId(), userId));
    }

    @Test
    public void updateTask_UsersAreNotInSystem() {
        Long userId = userDto1.id();
        UpdateTaskDto updateTaskDto;
        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .userId(userId)
                .build();
        Team team = Team.builder()
                .project(project)
                .teamMembers(new ArrayList<>(List.of(teamMember)))
                .id(1L)
                .build();
        project.getTeams().add(team);

        Mockito.when(taskRepository.findById(task.getId()))
                .thenReturn(Optional.ofNullable(task));
        Mockito.when(client.getUsersByIds(Mockito.anyList()))
                .thenReturn(new ArrayList<>(Arrays.asList(userDto1, null)));

        updateTaskDto = UpdateTaskDto.builder()
                .description("oops")
                .status(TaskStatus.TESTING)
                .build();
        Assertions.assertThrows(UserWasNotFoundException.class, () ->
                taskService.updateTask(updateTaskDto, task.getId(), userId));
    }

    @Test
    public void getTasksFilter_SuccessFilter() {
        TaskGettingDto taskGettingDto;
        Task taskTest = Task.builder()
                .id(1L)
                .name("something")
                .status(TaskStatus.TESTING)
                .build();
        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .userId(userDto1.id())
                .build();
        Team team = Team.builder()
                .project(project)
                .teamMembers(new ArrayList<>(List.of(teamMember)))
                .id(1L)
                .build();
        project.getTeams().add(team);

        project.getTasks().addAll(List.of(taskTest));

        Mockito.when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.ofNullable(project));


        taskGettingDto = TaskGettingDto.builder()
                .word("something")
                .status(TaskStatus.TESTING)
                .build();
        taskService.getTasksFilter(taskGettingDto, userDto1.id(), project.getId());
    }
}
