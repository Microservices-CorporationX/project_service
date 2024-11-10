package faang.school.projectservice.exception;

public class EntityNullException extends RuntimeException {
    public EntityNullException(String entityName) {
        super(MessageError.ENTITY_NULL_EXCEPTION.getMessage(entityName));
    }
}
