package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;

    @Transactional
    public void createStage(Stage stage) {
        stageRepository.save(stage);
    }

    public List<Stage> getMappedStages(List<StageDto> stages) {
        return stageMapper.toEntity(stages);
    }
}
