package faang.school.projectservice.exception;

import jakarta.persistence.EntityNotFoundException;

public class TeamMemberNotFoundException extends EntityNotFoundException {
    public TeamMemberNotFoundException(String message) {
        super(message);
    }
}
