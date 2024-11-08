package faang.school.projectservice.exception;

public class NonExistentDeletionTypeException extends RuntimeException {

    public NonExistentDeletionTypeException(String message) {
        super(message);
    }
}
