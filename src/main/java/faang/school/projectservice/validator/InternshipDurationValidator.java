package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.internShip.InternshipCreatedDto;
import faang.school.projectservice.exception.InternshipDurationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class InternshipDurationValidator {
    public void durationValidate(InternshipCreatedDto internShipCreatedDto) {
        LocalDateTime startDate = internShipCreatedDto.getStartDate();
        LocalDateTime endDate = internShipCreatedDto.getEndDate();

        int durationMonths = 3;

        if (ChronoUnit.MONTHS.between(startDate, endDate) > durationMonths ) {
            throw new InternshipDurationException("Internship duration cannot exceed 3 months");
        } else {
            log.info("End of Internship");
        }
    }
}
