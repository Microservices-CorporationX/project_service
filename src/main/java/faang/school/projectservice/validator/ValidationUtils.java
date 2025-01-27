package faang.school.projectservice.validator;

import org.springframework.stereotype.Component;

@Component
public class ValidationUtils {

    public static void validateNotBlank(String field, String fieldName) {
        if (field == null || field.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
    }

    public static void validateNotNull(Object field, String fieldName) {
        if (field == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
}