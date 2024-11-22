package faang.school.projectservice.exception;

public class StorageExceededException extends RuntimeException {
    public StorageExceededException(String message) {
        super(message);
    }
}
