package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageService {
    public final StageRepository stageRepository;
    public final StageMapper stageMapper;

    public StageDto createStage(StageDto stageDto) {
        Stage stage = stageMapper.toStage(stageDto);

        stage = stageRepository.save(stage);

        return stageMapper.toStageDto(stage);
    }
}
