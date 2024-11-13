package faang.school.projectservice.exceptions.invitation;

import jakarta.persistence.EntityNotFoundException;

public class TeamMemberNotFoundException extends EntityNotFoundException {
    public TeamMemberNotFoundException(Long teamMemberId) {
        super(String.format("Член команды не найден по идентификатору: %d", teamMemberId));
    }
}
