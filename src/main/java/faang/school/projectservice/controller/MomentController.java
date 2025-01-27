package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static faang.school.projectservice.utils.Constants.API_VERSION_1;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION_1 + "/moment")
public class MomentController {

    private final MomentService momentService;
    private final MomentControllerValidator momentControllerValidator;

    @PutMapping("/create")
    public MomentResponseDto create(@RequestBody MomentRequestDto momentRequestDto) {
        momentControllerValidator.validateName(momentRequestDto);
        momentControllerValidator.validateProjectIds(momentRequestDto);
        log.info("Created moment {}", momentRequestDto);
        return momentService.createMoment(momentRequestDto);
    }

    @PostMapping("/update")
    public MomentResponseDto update(@RequestBody MomentRequestDto momentRequestDto) {
        momentControllerValidator.validateName(momentRequestDto);
        log.info("Updated moment {}", momentRequestDto);
        return momentService.updateMoment(momentRequestDto);
    }

    @PostMapping("/filtered")
    public List<MomentResponseDto> getMoments(@RequestBody MomentFilterDto filter) {
        return momentService.getMoments(filter);
    }

    @GetMapping("/all")
    public List<MomentResponseDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping("/{id}")
    public MomentResponseDto getMoment(@PathVariable("id") Long momentId) {
        return momentService.getMoment(momentId);
    }



}
