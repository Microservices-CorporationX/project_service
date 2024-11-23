package faang.school.projectservice.controller.meet;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.service.meet.MeetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetController {

    private final MeetService meetService;

    @PostMapping
    public MeetDto createMeeting(@Validated(MeetDto.Before.class) @RequestBody MeetDto meetDto) {
        log.info("Received a request to create a meeting");
        return meetService.createMeeting(meetDto);
    }

    @PutMapping("/{meetId}")
    public MeetDto updateMeeting(@PathVariable Long meetId,
                                 @RequestHeader("x-user-id") Long userId,
                                 @Validated(MeetDto.After.class) @RequestBody MeetDto meetDto) {
        log.info("Received a request to update the meeting with ID: {}", meetId);
        return meetService.updateMeeting(meetId, userId, meetDto);
    }

    @DeleteMapping("/{meetId}")
    public void deleteMeeting(@PathVariable Long meetId,
                              @RequestHeader("x-user-id") Long userId) {
        log.info("Received a request to delete the meeting with ID: {} by the user with ID: {}", meetId, userId);
        meetService.deleteMeeting(meetId, userId);
    }

    @GetMapping("/projects/{projectId}")
    public List<MeetDto> getMeetingsByProjectFilteredByDateOrTitle(
            @PathVariable Long projectId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        log.info("Received a request to fetch all meetings of the project with ID: {} and filtered by date from {} to {} and/or by title: {}",
                projectId, title, dateFrom, dateTo);
        return meetService.getMeetingsByProjectFilteredByDateOrTitle(projectId, title, dateFrom, dateTo);
    }

    @GetMapping
    public List<MeetDto> getAllMeetings() {
        log.info("Received a request to fetch all meetings");
        return meetService.getAllMeetings();
    }

    @GetMapping("/{meetId}")
    public MeetDto getMeetingById(@PathVariable Long meetId) {
        log.info("Received a request to fetch the meeting with ID: {}", meetId);
        return meetService.getMeetingById(meetId);
    }
}
