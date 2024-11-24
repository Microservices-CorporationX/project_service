package faang.school.projectservice.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message, Exception ex) {
        super(message, ex);
    }

    public ForbiddenException(String message) {
        super(message);
    }
}