package ru.corporationx.projectservice.service.calendar;


import ru.corporationx.projectservice.client.UserServiceClient;
import ru.corporationx.projectservice.config.context.UserContext;
import faang.school.projectservice.model.dto.event.EventDto;
import ru.corporationx.projectservice.exception.AccessDeniedException;
import ru.corporationx.projectservice.model.entity.Project;
import ru.corporationx.projectservice.model.entity.TeamMember;
import ru.corporationx.projectservice.model.entity.TeamRole;
import ru.corporationx.projectservice.service.project.ProjectService;
import ru.corporationx.projectservice.service.teammember.TeamMemberService;
import feign.FeignException;
import feign.RetryableException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarService {
    private static final int RETRYABLE_DELAY = 1000;
    private static final int RETRYABLE_MULTIPLIER = 2;


    private final UserContext userContext;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final GoogleCalendarApiService apiService;
    private final UserServiceClient userServiceClient;

    public String createProjectCalendar(long projectId) {
        long userId = userContext.getUserId();
        TeamMember teamMember = checkIsOwnerWhenCreatingProject(userId, projectId);
        Project project = teamMember.getTeam().getProject();
        String calendarId = apiService.createCalendar(project.getName());
        project.setCalendarId(calendarId);
        projectService.save(project);
        log.info("Calendar created successfully with id: {}", calendarId);
        return calendarId;
    }

    @Retryable(
            retryFor = RetryableException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = RETRYABLE_DELAY, multiplier = RETRYABLE_MULTIPLIER))
    public String addEvent(long projectId, long eventId) {
        try {
            EventDto eventDto = userServiceClient.getEvent(eventId);
            long userId = userContext.getUserId();
            TeamMember teamMember = checkRolesWhenAddingEvent(userId, projectId);
            Project project = teamMember.getTeam().getProject();
            if (project.getCalendarId() == null) {
                log.warn("Can not add event to google calendar, calendar does not exist for project {}", project);
                throw new IllegalStateException("Google calendar for this project does not exist");
            }
            return apiService.addEventToCalendar(project.getCalendarId(),
                    eventDto.getTitle(), eventDto.getDescription(), eventDto.getStartDate(), eventDto.getEndDate());
        } catch (FeignException.NotFound e) {
            log.info("Event with id {} not found", eventId);
            throw new EntityNotFoundException("Event not found");
        }
    }

    public String addCalendarAccess(long projectId, String email, String role) {
        long userId = userContext.getUserId();
        TeamMember teamMember = checkRolesWhenAddingAclRule(userId, projectId);
        Project project = teamMember.getTeam().getProject();
        if (project.getCalendarId() == null) {
            log.warn("Cannot add ACL rule to google calendar, calendar does not exist for project {}", project);
            throw new IllegalStateException("Google calendar for this project does not exist");
        }
        String aclRuleId = apiService.addAclRule(project.getCalendarId(), email, role);
        log.info("Added ACL rule to calendar with id {} for user {} with role {}", project.getCalendarId(), email, role);
        return aclRuleId;
    }

    private TeamMember checkIsOwnerWhenCreatingProject(long userId, long projectId) {
        TeamMember teamMember = teamMemberService.validateUserIsProjectMember(userId, projectId);
        if (teamMember.getRoles() == null || !teamMember.getRoles().contains(TeamRole.OWNER)) {
            log.info("User with id {} tried to create google calendar for project with id {} but is not the owner",
                    userId, projectId);
            throw new AccessDeniedException("Only project owner can create google calendar for project");
        }
        return teamMember;
    }

    private TeamMember checkRolesWhenAddingEvent(long userId, long projectId) {
        TeamMember teamMember = teamMemberService.validateUserIsProjectMember(userId, projectId);
        if (teamMember.getRoles() == null || (!teamMember.getRoles().contains(TeamRole.MANAGER)
                && !teamMember.getRoles().contains(TeamRole.OWNER))) {
            log.info("User with id {} tried to create add event to google calendar for project with id {} " +
                            "but is not the owner or manager",
                    userId, projectId);
            throw new AccessDeniedException("Must be owner or manager to add event to google calendar");
        }
        return teamMember;
    }

    private TeamMember checkRolesWhenAddingAclRule(long userId, long projectId) {
        TeamMember teamMember = teamMemberService.validateUserIsProjectMember(userId, projectId);
        if (teamMember.getRoles() == null || !teamMember.getRoles().contains(TeamRole.OWNER)) {
            log.info("User with id {} tried to add ACL rule to google calendar for project with id {} but is not the owner",
                    userId, projectId);
            throw new AccessDeniedException("Only project owner can manage google calendar ACL");
        }
        return teamMember;
    }
}
