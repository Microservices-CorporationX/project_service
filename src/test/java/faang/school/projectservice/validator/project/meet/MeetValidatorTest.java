package faang.school.projectservice.validator.project.meet;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.UserDto;
import faang.school.projectservice.dto.project.meet.MeetDto;
import faang.school.projectservice.service.project.ProjectService;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetValidatorTest {

    @InjectMocks
    private MeetValidator meetValidator;

    @Mock
    private UserServiceClient userServiceClient;

    private MeetDto meetDto;
    private long userId;
    private ProjectService projectService;

    @BeforeEach
    public void setUp() {
        meetDto = new MeetDto();
        meetDto.setProjectId(1L);
        meetDto.setStartDateTime(LocalDateTime.now().minusDays(1));

        userId = 1L;

        projectService = mock(ProjectService.class);
    }

    @Test
    public void testValidateNotFoundUser() {
        when(userServiceClient.getUser(userId)).thenThrow(FeignException.class);

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> meetValidator.validate(meetDto, userId, projectService));
        assertEquals(String.format("user with id %d not found", userId), e.getMessage());
    }

    @Test
    public void testValidateNotFoundUserInProject() {
        when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
        when(projectService.hasUserInProject(meetDto.getProjectId(), userId)).thenReturn(false);

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> meetValidator.validate(meetDto, userId, projectService));

        assertEquals(
                String.format("user with id %d not found in project with id %d", userId, meetDto.getProjectId()),
                e.getMessage()
        );
    }

    @Test
    public void testValidateDateIsBeforeToday() {
        mockSuccessResult();

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> meetValidator.validate(meetDto, userId, projectService));
        assertEquals("start date time must be after now date", e.getMessage());
    }

    @Test
    public void testValidateStartDateIsBeforeEndDate() {
        mockSuccessResult();
        meetDto.setStartDateTime(LocalDateTime.now().plusDays(1));
        meetDto.setEndDateTime(LocalDateTime.now().plusHours(3));

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> meetValidator.validate(meetDto, userId, projectService));
        assertEquals("start date time must be after end date", e.getMessage());
    }

    @Test
    public void testValidateSuccessful() {
        meetDto.setStartDateTime(LocalDateTime.now().plusDays(1));
        meetDto.setEndDateTime(LocalDateTime.now().plusDays(2));
        mockSuccessResult();

        meetValidator.validate(meetDto, userId, projectService);
    }

    @Test
    public void testValidateWithProjectIdSuccessful() {
        long projectId = 1L;
        mockSuccessResult();

        meetValidator.validate(projectId, userId, projectService);
    }

    private void mockSuccessResult() {
        when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
        when(projectService.hasUserInProject(meetDto.getProjectId(), userId)).thenReturn(true);
    }
}
