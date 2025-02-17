package ru.corporationx.projectservice.validator.teammember;

import ru.corporationx.projectservice.client.UserServiceClient;
import ru.corporationx.projectservice.exception.DataValidationException;
import ru.corporationx.projectservice.exception.UserNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.corporationx.projectservice.model.dto.client.UserDto;
import ru.corporationx.projectservice.model.dto.teammember.TeamMemberDto;

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

