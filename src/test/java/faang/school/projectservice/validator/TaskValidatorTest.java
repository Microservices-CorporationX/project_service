package faang.school.projectservice.validator;

import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TaskValidatorTest {

    private TaskValidator taskValidator;

    private Project project;
    private Project otherProject;
    private long userId;
    private Team team;

    @BeforeEach
    public void setUp() {
        taskValidator = new TaskValidator();

        project = new Project();
        userId = 5L;

        long teamMemberId = 10L;
        TeamMember teamMember = TeamMember.builder()
                .userId(userId)
                .id(teamMemberId)
                .build();
        team = Team.builder()
                .teamMembers(List.of(teamMember))
                .build();

        String otherProjectName = "other name";
        long otherProjectId = 123L;
        otherProject = Project.builder()
                .name(otherProjectName)
                .id(otherProjectId)
                .build();
    }

    @Test
    public void testValidateProjectMembership() {
        // arrange
        project.setTeams(List.of(team));

        // act and assert
        assertDoesNotThrow(() ->
                taskValidator.validateProjectMembership(project, userId));
    }

    @Test
    public void testValidateProjectMembershipInvalidUser() {
        // arrange
        long fakeUserId = 25L;
        project.setTeams(List.of(team));

        // act and assert
        Assertions.assertThrows(AccessDeniedException.class,
                () -> taskValidator.validateProjectMembership(project, fakeUserId));
    }

    @Test
    public void testValidateParentTaskIsActive() {
        // arrange
        Task parentTask = Task.builder()
                .status(TaskStatus.IN_PROGRESS)
                .project(project)
                .build();
        Task task = Task.builder()
                .parentTask(parentTask)
                .project(project)
                .build();

        // act and assert
        assertDoesNotThrow(() -> taskValidator.validateParentTaskIsActive(task));
    }

    @Test
    public void testValidateParentTaskActiveInactiveParentTaskIs() {
        // arrange
        Task parentTask = Task.builder()
                .status(TaskStatus.DONE)
                .project(project)
                .build();
        Task task = Task.builder()
                .parentTask(parentTask)
                .project(project)
                .build();

        // act and assert
        Assertions.assertThrows(DataValidationException.class,
                () -> taskValidator.validateParentTaskIsActive(task));
    }

    @Test
    public void testValidateStageProjectMatches() {
        // arrange
        Stage stage = Stage.builder()
                .project(project)
                .build();
        Task task = Task.builder()
                .project(project)
                .build();

        // act and assert
        assertDoesNotThrow(() -> taskValidator.validateStageProjectMatches(stage, task));
    }

    @Test
    public void testValidateStageFromTheSameProjectStageFromOtherProjectMatches() {
        // arrange
        Stage stage = Stage.builder()
                .project(otherProject)
                .build();
        Task task = Task.builder()
                .project(project)
                .build();

        // act and assert
        assertThrows(DataValidationException.class,
                () -> taskValidator.validateStageProjectMatches(stage, task));
    }

    @Test
    public void testIsParentTaskFromTheSameProject() {
        // arrange
        Task parentTask = Task.builder()
                .project(project)
                .build();
        Task task = Task.builder()
                .project(project)
                .build();

        // act and assert
        assertDoesNotThrow(() -> taskValidator.validateParentTaskProjectMatches(parentTask, task));
    }

    @Test
    public void testIsParentTaskFromTheSameProjectDifferentProjects() {
        // arrange
        Task parentTask = Task.builder()
                .project(otherProject)
                .build();
        Task task = Task.builder()
                .project(project)
                .build();

        // act and assert
        assertThrows(DataValidationException.class,
                () -> taskValidator.validateParentTaskProjectMatches(parentTask, task));
    }
}
