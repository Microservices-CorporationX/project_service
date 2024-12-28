package faang.school.projectservice.service.calendar;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.event.EventDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleCalendarServiceTest {
    @Mock
    private UserContext userContext;
    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private ProjectService projectService;
    @Mock
    private GoogleCalendarApiService apiService;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private GoogleCalendarService googleCalendarService;

    private TeamMember teamMember;
    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setCalendarId("calendarId");

        teamMember = new TeamMember();
        teamMember.setUserId(1L);
        teamMember.setRoles(List.of(TeamRole.OWNER));
        teamMember.setTeam(new Team());
        teamMember.getTeam().setProject(project);
    }

    @Test
    void testCreateProjectCalendarCreated() {
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberService.validateUserIsProjectMember(1L, 1L)).thenReturn(teamMember);
        when(apiService.createCalendar(project.getName())).thenReturn("calendarId");
        String calendarId = googleCalendarService.createProjectCalendar(1L);
        assertEquals("calendarId", calendarId);
        verify(projectService).save(project);
    }

    @Test
    void testCreateProjectCalendarWithUserNotOwner() {
        teamMember.setRoles(List.of(TeamRole.MANAGER));
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberService.validateUserIsProjectMember(1L, 1L)).thenReturn(teamMember);
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> googleCalendarService.createProjectCalendar(1L));
        assertEquals("Only project owner can create google calendar for project", exception.getMessage());
    }

    @Test
    void testAddEventAdded() {
        EventDto eventDto = EventDto.builder()
                .title("title")
                .description("description")
                .startDate(LocalDateTime.now().minusHours(3))
                .endDate(LocalDateTime.now().plusHours(3))
                .build();
        when(userServiceClient.getEvent(1L)).thenReturn(eventDto);
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberService.validateUserIsProjectMember(1L, 1L)).thenReturn(teamMember);
        when(apiService.addEventToCalendar(
                "calendarId",
                eventDto.getTitle(),
                eventDto.getDescription(),
                eventDto.getStartDate(),
                eventDto.getEndDate())
        ).thenReturn("eventId");
        String eventId = googleCalendarService.addEvent(1L, 1L);
        assertEquals("eventId", eventId);
    }

    @Test
    void testAddEventWithUserNotManagerOrOwner() {
        teamMember.setRoles(List.of());
        when(userServiceClient.getEvent(1L)).thenReturn(new EventDto());
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberService.validateUserIsProjectMember(1L, 1L)).thenReturn(teamMember);
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> googleCalendarService.addEvent(1L, 1L));
        assertEquals("Must be owner or manager to add event to google calendar", exception.getMessage());
    }

    @Test
    void testAddEventWithEventNotFound() {
        FeignException.NotFound feignException = mock(FeignException.NotFound.class);
        when(userServiceClient.getEvent(1L)).thenThrow(feignException);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> googleCalendarService.addEvent(1L, 1L));
        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    void testAddCalendarAccessAdded() {
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberService.validateUserIsProjectMember(1L, 1L)).thenReturn(teamMember);
        when(apiService.addAclRule("calendarId", "user@example.com", "reader"))
                .thenReturn("aclRule");
        String aclRuleId = googleCalendarService.addCalendarAccess(1L, "user@example.com", "reader");
        assertEquals("aclRule", aclRuleId);
    }

    @Test
    void testAddCalendarAccessWithUserIsNotOwner() {
        teamMember.setRoles(List.of(TeamRole.DEVELOPER));
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberService.validateUserIsProjectMember(1L, 1L)).thenReturn(teamMember);
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> googleCalendarService.addCalendarAccess(1L, "user@example.com", "reader"));
        assertEquals("Only project owner can manage google calendar ACL", exception.getMessage());
    }

    @Test
    void testAddCalendarAccessWithCalendarNotExists() {
        teamMember.setRoles(List.of(TeamRole.OWNER));
        project.setCalendarId(null);
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberService.validateUserIsProjectMember(1L, 1L)).thenReturn(teamMember);
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> googleCalendarService.addCalendarAccess(1L, "user@example.com", "reader"));
        assertEquals("Google calendar for this project does not exist", exception.getMessage());
    }
}