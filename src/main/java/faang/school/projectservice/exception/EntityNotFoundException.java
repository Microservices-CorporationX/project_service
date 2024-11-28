package faang.school.projectservice.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Object entityId) {
        super(MessageError.ENTITY_NOT_FOUND_EXCEPTION.getMessage(entityName, entityId));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
