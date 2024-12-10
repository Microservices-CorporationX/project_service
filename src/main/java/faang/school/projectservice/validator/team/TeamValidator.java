package faang.school.projectservice.validator.team;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.exception.DataValidationException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeamValidator {

    private final UserServiceClient userServiceClient;

    public boolean validateTeam(TeamDto teamDto) {
        if (teamDto.getProjectId() == null) {
            log.error("The team project is null");
            throw new DataValidationException("The team must be related to some project");
        }
        if (teamDto.getTeamMemberIds().isEmpty()) {
            log.error("The team has no members");
            throw new DataValidationException("A team is not created without members");
        }
        return true;
    }

    public void validateUser(Long userId) {
        try {
            UserDto user = userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            log.error("User with ID {} does not exist.", userId);
            throw new IllegalArgumentException("User with given ID does not exist.");
        }
    }
}
