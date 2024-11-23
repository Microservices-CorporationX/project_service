package faang.school.projectservice.validator;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.exception.UnauthorizedAccessException;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j
public class MeetingValidator {
    public void validateMeetingUpdate(@Valid MeetDto updateMeetDto, long meetId, Meet meeting) {
        if (meeting.getStatus().equals(MeetStatus.CANCELLED) && updateMeetDto.getMeetStatus().equals(MeetStatus.CANCELLED)) {
            log.error("Meeting with ID {} is already cancelled", meetId);
            throw new RuntimeException("Meeting is already cancelled");
        } else {
            log.info("Meeting with ID {} is updated", meetId);
            meeting.setStatus(updateMeetDto.getMeetStatus());
            meeting.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        }

        if (!(meeting.getCreatorId() == updateMeetDto.getCreatorId())) {
            log.error("Unauthorized access to meet with ID {}", meetId);
            throw new UnauthorizedAccessException("Unauthorized access to meet with ID " + meetId);
        }
    }
}
