package faang.school.projectservice.controller.meet;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetRequestDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.service.meet.MeetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meets")
public class MeetController {

    private final MeetService meetService;
    private final UserContext userContext;

    @PostMapping
    public MeetResponseDto create(@Valid @RequestBody MeetRequestDto meetRequestDto) {
        long creatorId = userContext.getUserId();
        return meetService.createMeet(creatorId, meetRequestDto);
    }

    @PutMapping
    public MeetResponseDto update(@Valid @RequestBody MeetRequestDto meetRequestDto) {
        long creatorId = userContext.getUserId();
        return meetService.updateMeet(creatorId, meetRequestDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@Positive @PathVariable Long id) {
        long creatorId = userContext.getUserId();
        meetService.deleteMeet(creatorId, id);
    }

    @GetMapping("/projects/{projectId}")
    public List<MeetResponseDto> findAllByProjectIdFilter(@Positive @PathVariable Long projectId,
                                                          MeetFilterDto filter) {
        return meetService.findAllByProjectIdFilter(projectId, filter);
    }

    @GetMapping("/search")
    public List<MeetResponseDto> findMeets(
            @RequestParam(required = false) String titlePattern,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime createdFrom) {

        MeetFilterDto filter = MeetFilterDto.builder()
                .titlePattern(titlePattern)
                .createdAt(createdFrom)
                .build();

        return meetService.findByFilter(filter);
    }

    @GetMapping("/{id}")
    public MeetResponseDto findById(@Positive @PathVariable Long id) {
        return meetService.findById(id);
    }
}