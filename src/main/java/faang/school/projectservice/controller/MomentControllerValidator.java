package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MomentControllerValidator {
    void validateName(MomentRequestDto momentRequestDto) {
        if (StringUtils.isBlank(momentRequestDto.name())) {
            log.error("Name of moment cannot be empty!");
            throw new IllegalArgumentException("Name of moment cannot be empty!");
        }
    }

    void validateProjectIds(MomentRequestDto momentRequestDto) {
        if (momentRequestDto.projectToAddIds() == null || momentRequestDto.projectToAddIds().isEmpty()) {
            log.error("Moment must be associated at least to one project!");
            throw new IllegalArgumentException("Moment must be associated at least to one project!");
        }
    }
}
