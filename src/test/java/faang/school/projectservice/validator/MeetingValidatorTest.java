package faang.school.projectservice.validator;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.exception.UnauthorizedAccessException;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MeetingValidatorTest {
    @InjectMocks
    private MeetingValidator meetingValidator;

    @Mock
    private Meet meeting;

    @Mock
    private MeetDto updateMeetDto;

    private final long meetId = 1L;
    private final long creatorId = 2L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateMeetingUpdateWhenAlreadyCancelled() {
        when(meeting.getStatus()).thenReturn(MeetStatus.CANCELLED);
        when(updateMeetDto.getMeetStatus()).thenReturn(MeetStatus.CANCELLED);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            meetingValidator.validateMeetingUpdate(updateMeetDto, meetId, meeting);
        });

        assertEquals("Meeting is already cancelled", exception.getMessage());
        verify(meeting, never()).setStatus(any());
        verify(meeting, never()).setUpdatedAt(any());
    }

    @Test
    void testValidateMeetingUpdateWhenUnauthorizedAccess() {
        when(meeting.getCreatorId()).thenReturn(creatorId);
        when(updateMeetDto.getCreatorId()).thenReturn(3L);
        when(meeting.getStatus()).thenReturn(MeetStatus.COMPLETED);

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () -> {
            meetingValidator.validateMeetingUpdate(updateMeetDto, meetId, meeting);
        });

        assertEquals("Unauthorized access to meet with ID " + meetId, exception.getMessage());

        verify(meeting, never()).setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testValidateMeetingUpdateWhenSuccessfulUpdate() {
        when(meeting.getStatus()).thenReturn(MeetStatus.COMPLETED);
        when(updateMeetDto.getMeetStatus()).thenReturn(MeetStatus.COMPLETED);
        when(meeting.getCreatorId()).thenReturn(creatorId);
        when(updateMeetDto.getCreatorId()).thenReturn(creatorId);

        meetingValidator.validateMeetingUpdate(updateMeetDto, meetId, meeting);

        verify(meeting, times(1)).setStatus(MeetStatus.COMPLETED);
        verify(meeting, times(1)).setUpdatedAt(any(LocalDateTime.class));
    }
}
