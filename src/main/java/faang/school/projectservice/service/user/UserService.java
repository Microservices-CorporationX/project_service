package faang.school.projectservice.service.user;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.payment.UserClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserServiceClient userServiceClient;

    public boolean userExists(long userId) {
        ResponseEntity<Boolean> response = userServiceClient.userExists(userId);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new UserClientException("User client failed");
        }

        return Boolean.TRUE.equals(response.getBody());
    }
}
