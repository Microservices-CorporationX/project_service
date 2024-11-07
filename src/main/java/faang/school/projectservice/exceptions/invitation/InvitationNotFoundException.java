package faang.school.projectservice.exceptions.invitation;

public class InvitationNotFoundException extends RuntimeException {
    public InvitationNotFoundException(Long invitationId) {
        super("Приглашение с ID " + invitationId + " не найдено");
    }
}
