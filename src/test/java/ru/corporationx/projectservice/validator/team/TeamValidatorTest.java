package ru.corporationx.projectservice.validator.team;

import ru.corporationx.projectservice.client.UserServiceClient;
import ru.corporationx.projectservice.exception.DataValidationException;
import ru.corporationx.projectservice.exception.UserNotFoundException;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.corporationx.projectservice.model.dto.client.UserDto;
import ru.corporationx.projectservice.model.dto.team.TeamDto;
import ru.corporationx.projectservice.model.dto.teammember.TeamMemberDto;
import ru.corporationx.projectservice.validator.team.TeamValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private TeamValidator teamValidator;

    @Captor
    private ArgumentCaptor<UserDto> userCaptor;

    @Test
    void testValidateNullProjectId() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> teamValidator.validateTeam(TeamDto.builder().build()));

        assertEquals("The team must be related to some project", exception.getMessage());
    }

    @Test
    void testSuccessfulTeamValidation() {
        TeamDto teamDto = TeamDto.builder()
                .teamMembers(List.of(new TeamMemberDto()))
                .projectId(3L)
                .build();

        boolean result = teamValidator.validateTeam(teamDto);

        assertTrue(result);
    }

    @Test
    void testValidateIfUserNotFound() {
        when(userServiceClient.getUser(1L)).thenThrow(FeignException.NotFound.class);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> teamValidator.validateAuthor(1L));

        assertEquals("User with given ID does not exist.", exception.getMessage());
    }

    @Test
    void testSuccessfulUserValidation() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .build();
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        teamValidator.validateAuthor(userDto.getId());

        verify(userServiceClient).getUser(userDto.getId());
    }
}
