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
import org.springframework.web.bind.annotation.PatchMapping;
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


    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public MeetDto create(@Validated @RequestBody MeetDto meetDto) {
        return meetService.createMeet(meetDto);
    }

    @PutMapping("/update")
    public MeetDto update(@Validated @RequestBody UpdateMeetDto updateMeetDto, HttpServletRequest request) {
        return meetService.updateMeet(updateMeetDto, request);
    }

    @PatchMapping("/{id}/cancel")
    public MeetDto cancel(@PathVariable @Positive Long id, HttpServletRequest request) {
        return meetService.cancelMeet(id, request);
    }

    @DeleteMapping("/{meetId}/participants/delete/{userId}")
    public MeetDto delete(@PathVariable @Positive Long meetId,
                          @PathVariable @Positive Long userId,
                          HttpServletRequest request) {
        return meetService.deleteMeetingParticipant(meetId, userId, request);
    }

    @GetMapping("/{id}")
    public MeetDto get(@PathVariable @Positive Long id) {
        return meetService.getMeetById(id);
    }

    @GetMapping("/list")
    public List<MeetDto> getMeets(HttpServletRequest request) {
        return meetService.getMeets(request);
    }
}
