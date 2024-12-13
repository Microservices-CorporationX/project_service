package faang.school.projectservice.validator.teammember;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.exception.DataValidationException;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamMemberValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private TeamMemberValidator teamMemberValidator;

    @Test
    public void testValidateMembersShouldThrowExceptionWhenUserDoesNotExist() {
        TeamMemberDto memberDto = new TeamMemberDto();
        memberDto.setUserId(1L);
        when(userServiceClient.getUser(1L)).thenThrow(FeignException.NotFound.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                teamMemberValidator.validateMembers(Collections.singletonList(memberDto)));

        assertEquals("User with given ID does not exist.", exception.getMessage());
        verify(userServiceClient).getUser(1L);
    }

    @Test
    public void testValidateMembersShouldLogErrorWhenUserIsNull() {
        TeamMemberDto memberDto = new TeamMemberDto();
        memberDto.setUserId(1L);
        when(userServiceClient.getUser(1L)).thenReturn(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                teamMemberValidator.validateMembers(Collections.singletonList(memberDto)));

        assertEquals("User with given ID does not exist.", exception.getMessage());
        verify(userServiceClient).getUser(1L);
    }

    @Test
    public void testValidateMembersShouldNotThrowExceptionWhenUsersExist() {
        TeamMemberDto validMemberDto1 = new TeamMemberDto();
        validMemberDto1.setUserId(1L);
        TeamMemberDto validMemberDto2 = new TeamMemberDto();
        validMemberDto2.setUserId(2L);

        UserDto existingUser1 = new UserDto();
        UserDto existingUser2 = new UserDto();
        when(userServiceClient.getUser(1L)).thenReturn(existingUser1);
        when(userServiceClient.getUser(2L)).thenReturn(existingUser2);

        List<TeamMemberDto> teamMembers = List.of(validMemberDto1, validMemberDto2);

        assertDoesNotThrow(() -> teamMemberValidator.validateMembers(teamMembers));
        verify(userServiceClient).getUser(1L);
        verify(userServiceClient).getUser(2L);
    }

    @Test
    public void testValidateMembersShouldHandleDuplicateUserIds() {
        TeamMemberDto duplicateMember = new TeamMemberDto();
        duplicateMember.setUserId(1L);
        TeamMemberDto anotherMember = new TeamMemberDto();
        anotherMember.setUserId(2L);

        when(userServiceClient.getUser(anyLong())).thenReturn(new UserDto());

        assertDoesNotThrow(() -> teamMemberValidator.validateMembers(List.of(duplicateMember, duplicateMember, anotherMember)));
        verify(userServiceClient).getUser(1L);
        verify(userServiceClient).getUser(2L);
    }
}
