package faang.school.projectservice.service.stage;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageServiceImpl implements StageService {
    private final StageRepository stageRepository;

    @Override
    public Stage findById(Long id) {
        return stageRepository.getById(id);
    }
}
