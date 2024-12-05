package faang.school.projectservice.controller.google.calendar;

import faang.school.projectservice.dto.project.meet.MeetDto;
import faang.school.projectservice.dto.project.meet.MeetFilterDto;
import faang.school.projectservice.service.google.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;

    @GetMapping("/calendar/{meetId}")
    public MeetDto getEvent(@PathVariable long meetId) {
        return googleCalendarService.getEvent(meetId);
    }

    @PostMapping("{projectId}/calendar")
    public List<MeetDto> getEvents(@PathVariable long projectId, @RequestBody MeetFilterDto meetFilterDto) throws IOException {
        return googleCalendarService.getEvents(projectId, meetFilterDto);
    }

    @PostMapping("/calendar")
    @ResponseStatus(HttpStatus.CREATED)
    public MeetDto createEvent(@RequestBody @Validated MeetDto meetDto) throws IOException {
        return googleCalendarService.createEvent(meetDto);
    }

    @PutMapping("/calendar")
    public MeetDto updateEvent(@RequestBody @Validated MeetDto meetDto) throws IOException {
        return googleCalendarService.updateEvent(meetDto);
    }
}
