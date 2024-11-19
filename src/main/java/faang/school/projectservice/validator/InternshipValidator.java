package faang.school.projectservice.validator;

import faang.school.projectservice.dto.internship.InternshipCreatedDto;
import faang.school.projectservice.exception.InternshipDurationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class InternshipValidator {
    public void durationValidate(InternshipCreatedDto internShipCreatedDto) {
        LocalDateTime startDate = internShipCreatedDto.getStartDate();
        LocalDateTime endDate = internShipCreatedDto.getEndDate();
        int maxDurationMonths = 3;

        long internshipDuration = ChronoUnit.MONTHS.between(startDate, endDate);
        if (internshipDuration > maxDurationMonths) {
            log.error("Out of duration Internship");
            throw new InternshipDurationException("Internship duration cannot exceed " + maxDurationMonths + " months");
        }
    }
}
