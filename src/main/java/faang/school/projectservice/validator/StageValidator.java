package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.model.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageValidator {

    private final StageJpaRepository repository;

    public Stage validateStageExists(long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Stage",id));
    }
}
