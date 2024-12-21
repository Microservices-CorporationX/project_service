package faang.school.projectservice.validator.teammember;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.UserNotFoundException;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamMemberValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private TeamMemberValidator teamMemberValidator;

    @Test
    public void testValidateMembersIfTeamMembersIsNull() {
        List<TeamMemberDto> members = null;

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                teamMemberValidator.validateMembers(members));

        assertEquals("Team members list cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void testValidateMembersIfTeamMembersIsEmpty() {
        List<TeamMemberDto> members = List.of();

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                teamMemberValidator.validateMembers(members));

        assertEquals("Team members list cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void testValidateMembersIfThrowsUserNotFoundExceptionWhenUserNotFound() {
        List<TeamMemberDto> teamMembers = prepareTeamMembers();

        Request request = Request.create(Request.HttpMethod.GET, "/users",
                Collections.emptyMap(),
                new byte[0],
                StandardCharsets.UTF_8);

        when(userServiceClient.getUsersByIds(anyList()))
                .thenThrow(new FeignException.NotFound("User not found", request, new byte[0], null));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            teamMemberValidator.validateMembers(teamMembers);
        });

        assertEquals("User with given ID does not exist.", exception.getMessage());
    }

    private List<TeamMemberDto> prepareTeamMembers() {
        return List.of(
                TeamMemberDto.builder().id(1L).build(),
                TeamMemberDto.builder().id(2L).build()
        );
    }
}
