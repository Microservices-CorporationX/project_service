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
        List<Long> userIds = teamMembers.stream()
                .map(TeamMemberDto::getUserId)
                .distinct()
                .toList();

        for (Long userId : userIds) {
            try {
                UserDto user = userServiceClient.getUser(userId);
                if (user == null) {
                    log.error("User with ID {} does not exist.", userId);
                    throw new DataValidationException("User with given ID does not exist.");
                }
            } catch (FeignException.NotFound e) {
                log.error("User with ID {} does not exist.", userId);
                throw new IllegalArgumentException("User with given ID does not exist.");
            }
        }
    }
}
