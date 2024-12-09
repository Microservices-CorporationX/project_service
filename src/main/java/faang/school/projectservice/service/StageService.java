package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
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

    public List<Stage> getStagesByIds(List<Long> stageIds) {
        return stageRepository.findAllByIds(stageIds);
    }

    @Transactional
    public Stage getStage(long stageId) {
        return stageRepository.getById(stageId);
    }
}
