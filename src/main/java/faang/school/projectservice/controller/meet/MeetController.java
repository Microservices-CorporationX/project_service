package faang.school.projectservice.controller.meet;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.service.meet.MeetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetController {

    private final MeetService meetService;

    @PostMapping
    public MeetDto createMeeting(@Valid @RequestBody MeetDto meetDto) {
        log.info("");
        return meetService.createMeeting(meetDto);
    }

    @PatchMapping("/{meetId}")
    public MeetDto updateMeeting(@Valid @RequestBody MeetDto meetDto) {
        log.info("");
        return meetService.updateMeeting(meetDto);
    }

    @DeleteMapping("/{meetId}")
    public void deleteMeeting(@PathVariable Long meetId) {
        log.info("");
        meetService.deleteMeeting(meetId);
    }

    @GetMapping("/projects/{projectId}")
    public List<MeetDto> getMeetingsByProjectFilteredByDateOrTitle(@PathVariable Long projectId,
                                                                   @RequestParam(required = false) String title,
                                                                   @RequestParam(required = false)LocalDateTime date) {
        log.info("");
        return meetService.getMeetingsByProjectFilteredByDateOrTitle(projectId, title, date);
    }

    @GetMapping
    public List<MeetDto> getAllMeetings() {
        log.info("");
        return meetService.getAllMeetings();
    }

    @GetMapping("/{meetId}")
    public MeetDto getMeetingById(@PathVariable Long meetId) {
        log.info("");
        return meetService.getMeetingById(meetId);
    }
}
