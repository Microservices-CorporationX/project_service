package faang.school.projectservice.exception.invitation;

import jakarta.persistence.EntityNotFoundException;

public class InvalidInvitationDataException extends EntityNotFoundException {
    public InvalidInvitationDataException(String message) {
        super(message);
    }
}
