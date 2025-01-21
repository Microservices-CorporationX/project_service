package faang.school.projectservice.exeption;

public class ProjectNotClosableException extends RuntimeException {
    public ProjectNotClosableException(String message) {
        super(message);
    }
}
