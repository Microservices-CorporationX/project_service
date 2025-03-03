package ru.corporationx.projectservice.controller.calendar;

import ru.corporationx.projectservice.service.calendar.GoogleCalendarService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;

    @PostMapping("/projects/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    public String createProjectCalendar(@PathVariable long projectId) {
        return googleCalendarService.createProjectCalendar(projectId);
    }

    @PostMapping("/events/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    public String addEvent(@PathVariable long projectId, @PathParam("eventId") long eventId) {
        return googleCalendarService.addEvent(projectId, eventId);
    }

    @PostMapping("/acl-rules/{projectId}")
    public String addAclRule(@PathVariable long projectId, @PathParam("email") String email, @PathParam("role") String role) {
        return googleCalendarService.addCalendarAccess(projectId, email, role);
    }
}
