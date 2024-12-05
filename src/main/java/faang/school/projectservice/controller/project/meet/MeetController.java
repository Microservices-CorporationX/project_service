package faang.school.projectservice.controller.project.meet;

import faang.school.projectservice.dto.project.meet.MeetDto;
import faang.school.projectservice.dto.project.meet.MeetFilterDto;
import faang.school.projectservice.service.project.meet.MeetService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class MeetController {
    private final MeetService meetService;

    @GetMapping("/meets/{meetId}")
    public MeetDto getMeetById(@PathVariable long meetId) {
        return meetService.getMeetByIdAndUserId(meetId);
    }

    @PostMapping("/{projectId}/meets")
    public List<MeetDto> getMeetsByProjectId(@PathVariable long projectId, @RequestBody MeetFilterDto meetFilterDto) {
        return meetService.getMeets(projectId, meetFilterDto);
    }

    @PostMapping("/meets/create")
    @ResponseStatus(HttpStatus.CREATED)
    public MeetDto createMeet(@RequestBody @Validated MeetDto meetDto) {
        return meetService.createMeet(meetDto);
    }

    @PutMapping("/meets/update")
    public MeetDto updateMeet(@RequestBody @Validated MeetDto meetDto) {
        return meetService.updateMeet(meetDto);
    }
}
