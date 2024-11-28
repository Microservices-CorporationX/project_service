package faang.school.projectservice.exception;

public enum MessageError {
    ENTITY_NOT_FOUND_EXCEPTION("Entity %s with ID %s not found"),
    ALREADY_EXITS_EXCEPTION("Entity %s already exists"),
    DATA_VALIDATION_EXCEPTION("Invalid input data! %s"),
    EXTERNAL_SERVICE_UNEXPECTED_EXCEPTION("Unexpected error occurred while calling the external service. %s"),
    USER_UNAUTHORIZED_EXCEPTION("The user is unauthorized. %s"),
    FORBIDDEN_EXCEPTION("The user is forbidden from performing this operation. %s");

    private final String message;

    MessageError(String message) {
        this.message = message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}