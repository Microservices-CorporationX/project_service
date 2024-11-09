package faang.school.projectservice.model;

public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    REVIEW,
    TESTING,
    DONE,
    CANCELLED;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}