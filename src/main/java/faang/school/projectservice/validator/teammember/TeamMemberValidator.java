package faang.school.projectservice.validator.teammember;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.UserNotFoundException;
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

        try {
            List<UserDto> users = userServiceClient.getUsersByIds(userIds);
        } catch (FeignException.NotFound e) {
            log.error("User does not exist: {}", e.getMessage());
            throw new UserNotFoundException("User with given ID does not exist.");
        }
    }
}

