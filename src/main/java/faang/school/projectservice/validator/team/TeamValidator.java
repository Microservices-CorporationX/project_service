package faang.school.projectservice.validator.team;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.exception.DataValidationException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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
        return true;
    }

    public void validateAuthor(Long authorId) {
        try {
            UserDto user = userServiceClient.getUser(authorId);
        } catch (FeignException.NotFound e) {
            log.error("User with ID {} does not exist.", authorId);
            throw new IllegalArgumentException("User with given ID does not exist.");
        }
    }
}
