package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static faang.school.projectservice.utils.Constants.API_VERSION_1;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION_1 + "/subscription")
public class MomentController {
    private final MomentService momentService;
    private final ProjectService projectService;

    public void create(MomentDto momentDto) {
        validateMoment(momentDto);
        log.info("Created moment {}", momentDto);
        momentService.createMoment(momentDto);
    }

    public void update(MomentDto momentDto) {
        validateMoment(momentDto);
        log.info("Updated moment {}", momentDto);
        momentService.updateMoment(momentDto);
    }


    public void getMoments(MomentFilterDto filter) {
        momentService.getMoments(filter);
    }

    public void getAllMoments() {
        momentService.getAllMoments();
    }

    public void getMoment(Long momentId) {
        momentService.getMoment(momentId);
    }

    private void validateMoment(MomentDto momentDto) {
        if (momentDto.name().isBlank()) {
            log.error("Name of moment cannot be empty!");
            throw new IllegalArgumentException("Name of moment cannot be empty!");
        }
        if (momentDto.projectIds().isEmpty()) {
            log.error("Moment must be associated at least to one project!");
            throw new IllegalArgumentException("Moment must be associated at least to one project!");
        }
        //TODO сделать проверку, чтобы в массиве проектов были незакрытые проекты!
    }

}
