package faang.school.projectservice.validator.teammember;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.exception.DataValidationException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeamMemberValidator {

    private final UserServiceClient userServiceClient;

    public void validateMembers(List<TeamMemberDto> teamMembers) {
        if (teamMembers == null || teamMembers.isEmpty()) {
            throw new DataValidationException("Team members list cannot be null or empty.");
        }

        List<Long> userIds = teamMembers.stream()
                .map(TeamMemberDto::getUserId)
                .distinct()
                .toList();

        if (userIds.isEmpty()) {
            return;
        }

        try {
            List<UserDto> users = userServiceClient.getUsersByIds(userIds);
        } catch (FeignException.NotFound e) {
            log.error("User does not exist: {}", e.getMessage());
            throw new DataValidationException("User with given ID does not exist.");
        } catch (Exception e) {
            log.error("An error occurred while validating users: {}", e.getMessage());
            throw new DataValidationException("An error occurred during user validation.");
        }
    }
}

