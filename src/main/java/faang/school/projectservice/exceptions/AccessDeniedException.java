package faang.school.projectservice.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super("AccessDeniedException: " + message);
    }
}
