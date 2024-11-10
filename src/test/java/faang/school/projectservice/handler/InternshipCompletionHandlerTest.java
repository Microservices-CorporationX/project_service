package faang.school.projectservice.handler;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock
    private Task taskDone;

    @Mock
    private Task taskNotDone;

    @BeforeEach
    public void setUp() {

        taskDone = mock(Task.class);
        taskNotDone = mock(Task.class);

    }

    @Test
    public void testHandleInternsCompletionAllTasksDone_AssignNewRole() {
        internshipCompletionHandler.handleInternsCompletion(internship);

        assertFalse(intern.getRoles().contains(TeamRole.INTERN), "Intern role should have been changed from INTERN.");
    }

    @Test
    public void testHandleInternsCompletionNotAllTasksDone_RemoveFromTeam() {
        internshipCompletionHandler.handleInternsCompletion(internship);
        assertFalse(team.getTeamMembers().contains(intern), "Intern should be removed from the team.");
    }


    @Test
    public void testInternsToDismissalNullOrEmptyList() {
        internshipCompletionHandler.internsToDismissal(null);

        List<Long> internIds = new ArrayList<>();
        internshipCompletionHandler.internsToDismissal(internIds);
        assertTrue(internIds.isEmpty(), "Interns list should remain empty when passed an empty list.");
    }

    @Test
    public void testRemoveInternFromTeamWhenTeamIsNull() {
        internshipCompletionHandler.handleInternsCompletion(internship);
        verify(intern, never()).getTeam();
    }
}
