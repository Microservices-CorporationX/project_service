package faang.school.projectservice.service;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StageService {

    private final StageRepository stageRepository;

    public List<Stage> getStagesByIds(List<Long> stageIds) {
        return stageIds.stream()
                .map(stageRepository::getById)
                .toList();
    }
}
