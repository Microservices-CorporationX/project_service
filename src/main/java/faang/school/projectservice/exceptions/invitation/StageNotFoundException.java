package faang.school.projectservice.exceptions.invitation;

import jakarta.persistence.EntityNotFoundException;

public class StageNotFoundException extends EntityNotFoundException {
    public StageNotFoundException(Long stageId) {
        super(String.format("Этап с идентификатором %d не найден", stageId));
    }
}
