package faang.school.projectservice.exception;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String entityName) {
        super("Entity " + entityName + " already exists");
    }
}
