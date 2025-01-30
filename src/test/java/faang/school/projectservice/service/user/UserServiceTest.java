package faang.school.projectservice.service.user;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.payment.UserClientException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private UserService userService;

    private final long testUserId = 123L;

    @Test
    public void userExists_ShouldReturnTrue_WhenClientReturnsTrue() {
        when(userServiceClient.userExists(testUserId))
                .thenReturn(new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK));

        boolean result = userService.userExists(testUserId);

        assertTrue(result);
        verify(userServiceClient).userExists(testUserId);
    }

    @Test
    public void userExists_ShouldThrowException_WhenNonOkStatus() {
        when(userServiceClient.userExists(testUserId))
                .thenReturn(new ResponseEntity<>(Boolean.TRUE, HttpStatus.INTERNAL_SERVER_ERROR));

        UserClientException exception = assertThrows(UserClientException.class,
                () -> userService.userExists(testUserId));

        assertEquals("User client failed", exception.getMessage());
        verify(userServiceClient).userExists(testUserId);
    }
}
