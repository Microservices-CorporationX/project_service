package faang.school.projectservice.validator.task;

import faang.school.projectservice.model.Task;

public interface TaskValidator {
    void validateTaskExistence(long id);
}
