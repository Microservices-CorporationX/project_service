package faang.school.projectservice.exeption;
public class NotUniqueProjectException extends RuntimeException{
    public NotUniqueProjectException (String message) {
        super(message);
    }
}
