package faang.school.projectservice.exceptions.invitation;

public class StageNotFoundException extends RuntimeException{
    public StageNotFoundException(Long stageId) {
        super(String.format("Этап с идентификатором %d не найден", stageId));
    }
}
