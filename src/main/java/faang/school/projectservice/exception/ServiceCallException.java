package faang.school.projectservice.exception;

public class ServiceCallException extends RuntimeException {
    public ServiceCallException(String message, Exception e) {
        super(message, e);
    }
}
