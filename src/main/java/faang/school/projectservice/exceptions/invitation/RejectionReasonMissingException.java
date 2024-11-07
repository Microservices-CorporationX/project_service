package faang.school.projectservice.exceptions.invitation;

public class RejectionReasonMissingException extends RuntimeException {
    public RejectionReasonMissingException() {
        super("Причина отклонения обязательна");
    }
}
