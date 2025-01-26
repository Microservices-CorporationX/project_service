package faang.school.projectservice.exception;

public class DataValidationException extends IllegalArgumentException {
    public DataValidationException(String message) {
        super(message);
    }
}
