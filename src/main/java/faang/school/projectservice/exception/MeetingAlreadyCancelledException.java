package faang.school.projectservice.exception;

public class MeetingAlreadyCancelledException extends RuntimeException {
    public MeetingAlreadyCancelledException(String message) {
        super(message);
    }
}
