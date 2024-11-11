package faang.school.projectservice.exception;

public enum MessageError {
    ENTITY_NOT_FOUND_EXCEPTION("Entity %s with ID %s not found"),
    ALREADY_EXITS_EXCEPTION("Entity %s already exists");

    private final String message;

    MessageError(String message) {
        this.message = message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
