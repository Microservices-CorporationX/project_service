package faang.school.projectservice.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message, Exception ex) {
        super(message, ex);
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}