package faang.school.projectservice.handler;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class InternshipCompletionHandlerTest {

    @InjectMocks
    private InternshipCompletionHandler internshipCompletionHandler;

    @Mock
    private Internship internship;

    @Mock
    private TeamMember intern;

    @Mock
    private Team team;


    private List<Task> tasks;
    private List<Stage> stages;

    @BeforeEach
    public void setUp() {
        Task taskDone1 = new Task();
        taskDone1.setStatus(TaskStatus.DONE);

        Task taskDone2 = new Task();
        taskDone2.setStatus(TaskStatus.DONE);

        Task taskNotDone = new Task();
        taskNotDone.setStatus(TaskStatus.IN_PROGRESS);

        tasks = new ArrayList<>();
        tasks.add(taskDone1);
        tasks.add(taskDone2);

        Stage stage = new Stage();
        stage.setTasks(tasks);
        stages = List.of(stage);
    }

    @Test
    public void testProcessInternshipCompletionWithCompletedStatus_ChangesRoleOrRemovesFromTeam() {
        when(intern.getStages()).thenReturn(stages);
        when(intern.getRoles()).thenReturn(new ArrayList<>(List.of(TeamRole.INTERN)));
        when(internship.getInterns()).thenReturn(List.of(intern));

        internshipCompletionHandler.processInternshipCompletion(internship, InternshipStatus.COMPLETED);

        assertFalse(intern.getRoles().contains(TeamRole.INTERN), "Role should have been changed from INTERN.");
        assertTrue(intern.getRoles().contains(TeamRole.DEVELOPER), "Role should be DEVELOPER.");
        verify(team, never()).getTeamMembers();
    }

    @Test
    public void testProcessInternshipCompletionWithCompletedStatus_RemovesInternFromTeamIfTasksIncomplete() {
        tasks.get(1).setStatus(TaskStatus.IN_PROGRESS);
        when(intern.getStages()).thenReturn(stages);
        when(intern.getTeam()).thenReturn(team);

        List<TeamMember> teamMembers = new ArrayList<>(List.of(intern));
        List<TeamMember> mockTeamMembers = mock(List.class);

        when(team.getTeamMembers()).thenReturn(mockTeamMembers);
        when(internship.getInterns()).thenReturn(List.of(intern));

        internshipCompletionHandler.processInternshipCompletion(internship, InternshipStatus.COMPLETED);

        verify(mockTeamMembers).remove(intern);
    }

    @Test
    public void testProcessInternshipCompletionWithNonCompletedStatus_NoActionTaken() {
        internshipCompletionHandler.processInternshipCompletion(internship, InternshipStatus.IN_PROGRESS);

        verify(intern, never()).getStages();
        verify(team, never()).getTeamMembers();
    }
}
