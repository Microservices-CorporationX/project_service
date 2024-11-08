package faang.school.projectservice.exception;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String entityName) {
        super(MessageError.ALREADY_EXITS_EXCEPTION.getMessage(entityName));
    }
}
