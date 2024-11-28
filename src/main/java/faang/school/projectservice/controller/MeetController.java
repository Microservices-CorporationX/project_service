package faang.school.projectservice.controller;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.service.MeetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/meet")
@Validated
public class MeetController {
    private final MeetService meetService;


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public MeetDto create(@Validated @RequestBody MeetDto meetDto) {
        return meetService.createMeet(meetDto);
    }

    @PutMapping("/{id}")
    public MeetDto update(@PathVariable @Positive long id,
                          @Validated @RequestBody UpdateMeetDto updateMeetDto) {
        return meetService.updateMeet(id, updateMeetDto);
    }

    @DeleteMapping("/{meetId}/participants/{userId}")
    public MeetDto delete(@PathVariable @Positive long meetId,
                          @PathVariable @Positive long userId) {
        return meetService.deleteMeetingParticipant(meetId, userId);
    }

    @GetMapping("/{id}")
    public MeetDto get(@PathVariable @Positive long id) {
        return meetService.getMeetById(id);
    }

    @GetMapping("/list")
    public List<MeetDto> getMeets(HttpServletRequest request) {
        return meetService.getMeets(request);
    }
}
