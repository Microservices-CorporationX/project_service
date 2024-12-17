package faang.school.projectservice.validator.task;

import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class TaskValidator {

    public void validateTaskIdIsNull(Long taskId) {
        if (taskId != null) {
            throw new DataValidationException("Task id should be empty");
        }
    }

    public void validateTaskIdIsNotNull(Long taskId) {
        if (taskId == null) {
            throw new DataValidationException("Task id can't be empty");
        }
    }
}
