package faang.school.projectservice.validator;


import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetValidatorTest {
    @InjectMocks
    private MeetValidator meetValidator;
    @Mock
    private UserServiceClient userServiceClient;
    private Meet meet;
    private UpdateMeetDto updateMeetDto;
    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        meetValidator = new MeetValidator(userServiceClient);
        meet = new Meet();
        meet.setId(1L);
        meet.setCreatorId(1L);
        List<Long> userIds = new ArrayList<>() {{
            add(1L);
            add(2L);
            add(3L);
        }};
        meet.setUserIds(userIds);
        updateMeetDto = UpdateMeetDto.builder()
                .id(1L)
                .title("new title")
                .description("new desc")
                .userIds(List.of(1L, 2L))
                .status(MeetStatus.COMPLETED)
                .build();
        Meet secondMeet = new Meet();
        secondMeet.setId(2L);
        secondMeet.setTitle("any title");
        secondMeet.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void testValidateMeetUpdatingWithCompletedStatus() {
        meet.setStatus(MeetStatus.COMPLETED);
        assertThrows(DataValidationException.class, () -> meetValidator.validateMeetUpdating(meet));
    }

    @Test
    public void testValidateMeetUpdatingWithCancelledStatus() {
        meet.setStatus(MeetStatus.CANCELLED);
        assertThrows(DataValidationException.class, () -> meetValidator.validateMeetUpdating(meet));
    }

    @Test
    public void testValidateThatRequestWasSentByTheCreator() {
        when(request.getHeader("x-user-id")).thenReturn("1");
        meetValidator.validateThatRequestWasSentByTheCreator(meet, request);
        verify(request, times(1)).getHeader("x-user-id");
    }

    @Test
    public void testValidateThatRequestWasSentByTheCreatorNegative() {
        when(request.getHeader("x-user-id")).thenReturn("100");
        assertThrows(DataValidationException.class, () ->
                meetValidator.validateThatRequestWasSentByTheCreator(meet, request));
    }

    @Test
    public void testValidateUserExists() {
        when(userServiceClient.getUser(anyLong())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () ->
                meetValidator.validateUserExists(123L));
    }

    @Test
    public void testValidateParticipants() {
        Long participantId = 1L;
        meetValidator.validateParticipants(participantId, meet);
    }

    @Test
    public void testValidateParticipantsWithNotParticipantId() {
        Long participantId = 6312L;
        assertThrows(DataValidationException.class, () ->
                meetValidator.validateParticipants(participantId, meet));
    }
}
