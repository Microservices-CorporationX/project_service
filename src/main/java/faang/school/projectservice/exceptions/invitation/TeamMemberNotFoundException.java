package faang.school.projectservice.exceptions.invitation;

public class TeamMemberNotFoundException extends RuntimeException {
    public TeamMemberNotFoundException(Long teamMemberId) {
        super(String.format("Член команды не найден по идентификатору: %d", teamMemberId));
    }
}
