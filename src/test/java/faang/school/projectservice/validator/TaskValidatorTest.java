package faang.school.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.TeamMemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskValidatorTest {
    @InjectMocks
    private TaskValidator validator;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private StageService stageService;

    @Mock
    private TeamMemberService teamMemberService;

    @Test
    void validateStingShouldThrowExceptionWhenTextIsNull() {
        assertThrows(DataValidationException.class, () -> validator.validateString(null),
                "Expected DataValidationException for null text");
    }

    @Test
    void validateStingShouldThrowExceptionWhenTextIsBlank() {
        assertThrows(DataValidationException.class, () -> validator.validateString("   "),
                "Expected DataValidationException for blank text");
    }

    @Test
    void validateStingShouldPassWhenTextIsValid() {
        assertDoesNotThrow(() -> validator.validateString("Valid text"));
    }

    @Test
    void validateStatusShouldThrowExceptionWhenStatusIsNull() {
        assertThrows(DataValidationException.class,
                () -> validator.validateStatus(null),
                "Expected DataValidationException for null status");
    }

    @Test
    void validateStatusShouldPassWhenStatusIsValid() {
        TaskStatus validStatus = TaskStatus.valueOf("TODO");
        assertDoesNotThrow(() -> validator.validateStatus(validStatus));
    }

    @Test
    void validateUserShouldThrowExceptionWhenUserIdIsNull() {
        assertThrows(DataValidationException.class,
                () -> validator.validateUser(null),
                "Expected DataValidationException for null userId");
    }

    @Test
    void validateUserShouldThrowExceptionWhenUserDoesNotExist() {
        Long userId = 123L;
        when(userServiceClient.getUser(userId)).thenReturn(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validateUser(userId));
        assertEquals("User with id 123 does not exist", exception.getMessage());
    }

    @Test
    void validateUser_shouldPassWhenUserExists() {
        Long userId = 123L;
        when(userServiceClient.getUser(userId)).thenReturn(new UserDto());

        assertDoesNotThrow(() -> validator.validateUser(userId));
    }

    @Test
    void validateTaskShouldThrowExceptionWhenTaskDoesNotExist() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validateTask(taskId));
        assertEquals("Task with id 1 does not exist", exception.getMessage());
    }

    @Test
    void validateTaskShouldPassWhenTaskExists() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(new Task()));

        assertDoesNotThrow(() -> validator.validateTask(taskId));
    }

    @Test
    void validateProjectShouldThrowExceptionWhenProjectIdIsNull() {
        assertThrows(DataValidationException.class,
                () -> validator.validateProject(null),
                "Expected exception for null projectId");
    }

    @Test
    void validateProjectShouldThrowExceptionWhenProjectDoesNotExist() {
        Long projectId = 1L;
        when(projectService.getProjectById(projectId)).thenReturn(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validateProject(projectId));
        assertEquals("Project with id 1 does not exist", exception.getMessage());
    }

    @Test
    void validateProjectShouldPassWhenProjectExists() {
        Long projectId = 1L;
        Project mockProject = new Project();
        when(projectService.getProjectById(projectId)).thenReturn(mockProject);

        assertDoesNotThrow(() -> validator.validateProject(projectId));
    }

    @Test
    void validateStageShouldThrowExceptionWhenStageIdIsNull() {
        assertThrows(DataValidationException.class,
                () -> validator.validateStage(null),
                "Expected exception for null stageId");
    }

    @Test
    void validateStageShouldThrowExceptionWhenStageDoesNotExist() {
        Long stageId = 1L;
        when(stageService.getById(stageId)).thenReturn(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validateStage(stageId));
        assertEquals("Stage with id 1 does not exist", exception.getMessage());
    }

    @Test
    void validateStageShouldPassWhenStageExists() {
        Long stageId = 1L;
        Stage mockStage = new Stage();
        when(stageService.getById(stageId)).thenReturn(mockStage);

        assertDoesNotThrow(() -> validator.validateStage(stageId));
    }

    @Test
    void validateTeamMemberShouldPassWhenMemberExistsInProject() {
        Long projectId = 1L;
        Long teamMemberId = 2L;

        TeamMember mockMember = new TeamMember();
        mockMember.setId(teamMemberId);

        Team mockTeam = new Team();
        mockTeam.setTeamMembers(List.of(mockMember));

        Project mockProject = new Project();
        mockProject.setTeams(List.of(mockTeam));

        when(projectService.getProjectById(projectId)).thenReturn(mockProject);

        assertDoesNotThrow(() -> validator.validateTeamMember(teamMemberId, projectId));
    }
}