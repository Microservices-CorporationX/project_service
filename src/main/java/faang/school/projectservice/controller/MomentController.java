package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import faang.school.projectservice.service.ProjectService;
import io.micrometer.common.util.StringUtils;
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
    private final ProjectService projectService;

    @PutMapping("/create")
    public MomentDto create(MomentDto momentDto) {
        validateMoment(momentDto);
        log.info("Created moment {}", momentDto);
        return momentService.createMoment(momentDto);
    }

    @PostMapping("/update")
    public MomentDto update(MomentDto momentDto) {
        validateMoment(momentDto);
        log.info("Updated moment {}", momentDto);
        return momentService.updateMoment(momentDto);
    }

    public List<MomentDto> getMoments(MomentFilterDto filter) {
        return momentService.getMoments(filter);
    }

    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    public MomentDto getMoment(Long momentId) {
        return momentService.getMoment(momentId);
    }

    private void validateMoment(MomentDto momentDto) {
        if (StringUtils.isBlank(momentDto.name())) {
            log.error("Name of moment cannot be empty!");
            throw new IllegalArgumentException("Name of moment cannot be empty!");
        }
        if (momentDto.projectIds() == null || momentDto.projectIds().isEmpty()) {
            log.error("Moment must be associated at least to one project!");
            throw new IllegalArgumentException("Moment must be associated at least to one project!");
        }
    }

}
