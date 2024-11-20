package faang.school.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class MeetValidator {
    private final UserServiceClient userServiceClient;

    public void validateMeetUpdating(Meet meet) {
        if (meet.getStatus().equals(MeetStatus.CANCELLED) || meet.getStatus().equals(MeetStatus.COMPLETED)) {
            log.error("Trying to update cancelled or completed meet: {}", meet);
            throw new DataValidationException("Meet already cancelled or completed");
        }
    }

    public void validateThatRequestWasSentByTheCreator(Meet meet, HttpServletRequest request) {
        long requesterId = Long.parseLong(request.getHeader("x-user-id"));
        validateUserExists(requesterId);

        if (meet.getCreatorId() != requesterId) {
            log.error("User with id {} is not the creator of the meeting {}", requesterId, meet);
            throw new DataValidationException("Only the team member who created the meet can update it");
        }
    }

    public void validateUserExists(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new EntityNotFoundException("User with id %s does not exist".formatted(userId));
        }
    }

    public void validateParticipants(Long userId, Meet meet) {
        List<Long> participants = meet.getUserIds();
        if (!participants.contains(userId)) {
            throw new DataValidationException("User with id %s is not a participant in the meeting".formatted(userId));
        }
    }
}

