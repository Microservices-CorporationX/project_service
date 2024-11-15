package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.filter.stage.StageNameFilter;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.service.task.TaskService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageJpaRepository stageJpaRepository;
    private final StageMapper stageMapper;
    private final List<StageFilter> stageFilterList;
    private final TaskService taskService;

    public StageDto createStage(StageDto stageDto) {

        Stage stage = stageMapper.toStage(stageDto);

        return stageMapper.toStageDto(stageJpaRepository.save(stage));
    }

    public List<StageDto> getAllStagesByFilters(StageFilterDto stageFilterDto) {
        Stream<Stage> stageStream = stageJpaRepository.findAll().stream();
        return stageFilterList.stream()
                .filter(filter -> filter.isApplicable(stageFilterDto))
                .reduce(stageStream,
                        (stream, filter) -> filter.apply(stream, stageFilterDto),
                        (s1, s2) -> s1)
                .map(stageMapper::toStageDto)
                .toList();
    }

    public StageDto deleteStageById(Long id) {
        Stage stage = stageJpaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Stage not found"));
        taskService.saveAll(stage.getTasks().stream()
                .peek(task -> task.setStatus(TaskStatus.CANCELLED))
                .toList());
        stageJpaRepository.deleteById(id);
        return stageMapper.toStageDto(stage);
    }

    public StageDto updateStage(StageDto stageDto) {

        return null;
    }
}
