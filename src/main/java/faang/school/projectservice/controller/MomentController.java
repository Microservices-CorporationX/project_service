package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static faang.school.projectservice.utils.Constants.API_VERSION_1;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION_1 + "/moment")
public class MomentController {
    private final MomentService momentService;
    private final ProjectService projectService;

    @PutMapping("/create")
    public MomentResponseDto create(MomentRequestDto momentRequestDto) {
        validateMomentRequestDto(momentRequestDto);
        log.info("Created moment {}", momentRequestDto);
        return momentService.createMoment(momentRequestDto);
    }

    @PostMapping("/update")
    public MomentResponseDto update(MomentRequestDto momentRequestDto) {
        validateMomentRequestDto(momentRequestDto);
        log.info("Updated moment {}", momentRequestDto);
        return momentService.updateMoment(momentRequestDto);
    }

    public List<MomentResponseDto> getMoments(MomentFilterDto filter) {
        return momentService.getMoments(filter);
    }

    public List<MomentResponseDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    public MomentResponseDto getMoment(Long momentId) {
        return momentService.getMoment(momentId);
    }

    private void validateMomentRequestDto(MomentRequestDto momentRequestDto) {
        if (StringUtils.isBlank(momentRequestDto.name())) {
            log.error("Name of moment cannot be empty!");
            throw new IllegalArgumentException("Name of moment cannot be empty!");
        }
        if (momentRequestDto.projectIds() == null || momentRequestDto.projectIds().isEmpty()) {
            log.error("Moment must be associated at least to one project!");
            throw new IllegalArgumentException("Moment must be associated at least to one project!");
        }
    }

}
