package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentCreateRequestDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentUpdateRequestDto;
import faang.school.projectservice.service.MomentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/moment")
public class MomentController {

    private final MomentService momentService;

    @PostMapping("/")
    public MomentResponseDto create(@RequestBody MomentCreateRequestDto momentCreateRequestDto) {
        log.info("Created moment {}", momentCreateRequestDto);
        return momentService.createMoment(momentCreateRequestDto);
    }

    @PatchMapping("/")
    public MomentResponseDto update(@PathVariable("id") Long momentId,
                                    @RequestBody MomentUpdateRequestDto momentUpdateRequestDto) {
        log.info("Updated moment {}", momentUpdateRequestDto);
        return momentService.updateMoment(momentId, momentUpdateRequestDto);
    }

    @PostMapping("/filtered")
    public List<MomentResponseDto> getMoments(@RequestBody MomentFilterDto filter) {
        //@RequestHeader(value = "X-User-Id", required = true) Long userId,
        return momentService.getMoments(filter);
    }

    @GetMapping("/")
    public List<MomentResponseDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping("/{id}")
    public MomentResponseDto getMoment(@PathVariable("id") Long momentId) {
        return momentService.getMoment(momentId);
    }



}
