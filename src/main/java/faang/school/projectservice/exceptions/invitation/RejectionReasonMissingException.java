package faang.school.projectservice.exceptions.invitation;

public class RejectionReasonMissingException extends RuntimeException {
    public RejectionReasonMissingException(String message) {
        super("Причина отклонения обязательна");
    }
}
