package faang.school.projectservice.exceptions.invitation;

import jakarta.persistence.EntityNotFoundException;

public class InvalidInvitationDataException extends EntityNotFoundException {
    public InvalidInvitationDataException(String message) {
        super(message);
    }
}
