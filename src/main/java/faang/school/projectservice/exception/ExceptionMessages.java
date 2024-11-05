package faang.school.projectservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessages {
    PROJECT_NOT_FOUND("Project with id %d not found"),
    WRONG_PROJECT_STATUS("Project status can't be cancelled or completed. Project id %d"),
    PROJECT_STATUS_CANCELLED("Project has status CANCELLED");

    private final String message;
}