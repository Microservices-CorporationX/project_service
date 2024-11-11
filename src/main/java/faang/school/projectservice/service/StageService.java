package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectService projectService;

    public StageDto create(StageDto stage) {
        validate(stage);

    }

    private void validate(StageDto stageDto) {
        if (!projectService.existsById(stageDto.getProjectId())){
            log.error("Не существующий проект");
            throw new DataValidationException("Не существующий проект");
        }
    }
}
