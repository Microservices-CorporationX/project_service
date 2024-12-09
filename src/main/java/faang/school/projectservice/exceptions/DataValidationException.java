package faang.school.projectservice.exceptions;

public class DataValidationException extends RuntimeException {

    public DataValidationException() {
        super();
    }

    public DataValidationException(String message) {
        super("DataValidationException: " + message);
    }

    public DataValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataValidationException(Throwable cause) {
        super(cause);
    }
}
