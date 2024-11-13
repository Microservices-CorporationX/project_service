package faang.school.projectservice.exceptions.invitation;

import jakarta.persistence.EntityNotFoundException;

public class InvitationNotFoundException extends EntityNotFoundException {
    public InvitationNotFoundException(String message) {
        super(message);
    }
}
