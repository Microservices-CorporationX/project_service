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
    public void createProjectCalendar(@PathVariable long projectId) {
        googleCalendarService.createProjectCalendar(projectId);
    }

    @PostMapping("/event/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addEvent(@PathVariable long projectId, @PathParam("eventId") long eventId) {
        googleCalendarService.addEvent(projectId, eventId);
    }
}
