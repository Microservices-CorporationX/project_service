package faang.school.projectservice.controller.calendar;

import faang.school.projectservice.service.calendar.GoogleCalendarService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;

    @PostMapping("/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    public String createProjectCalendar(@PathVariable long projectId) {
        return googleCalendarService.createProjectCalendar(projectId);
    }

    @PostMapping("/event/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    public String addEvent(@PathVariable long projectId, @PathParam("eventId") long eventId) {
        return googleCalendarService.addEvent(projectId, eventId);
    }

    @PostMapping("/acl/{projectId}")
    public String addAclRule(@PathVariable long projectId, @PathParam("email") String email, @PathParam("role") String role) {
        return googleCalendarService.addCalendarAccess(projectId, email, role);
    }
}
