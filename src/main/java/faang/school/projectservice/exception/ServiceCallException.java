package faang.school.projectservice.exception;

public class ServiceCallException extends RuntimeException {

    public ServiceCallException(String message, Exception ex) {
        super(message, ex);
    }

    public ServiceCallException(String message) {
        super(message);
    }
}
