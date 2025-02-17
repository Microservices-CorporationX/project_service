package ru.corporationx.projectservice.validator.team;

import ru.corporationx.projectservice.client.UserServiceClient;
import ru.corporationx.projectservice.exception.DataValidationException;
import ru.corporationx.projectservice.exception.UserNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.corporationx.projectservice.model.dto.client.UserDto;
import ru.corporationx.projectservice.model.dto.team.TeamDto;


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
            throw new UserNotFoundException("User with given ID does not exist.");
        }
    }
}
