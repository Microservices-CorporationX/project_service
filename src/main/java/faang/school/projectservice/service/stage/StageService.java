package faang.school.projectservice.service.stage;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.model.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageJpaRepository stageRepository;

    public Stage getStageEntity(long stageId) {
        return stageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage", stageId));
    }
}
