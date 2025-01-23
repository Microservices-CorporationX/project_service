package faang.school.projectservice.service;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;

    public Stage findById(@NotNull Long stageId) {
        return stageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Этап c id: %d не найден", stageId)));
    }
}
