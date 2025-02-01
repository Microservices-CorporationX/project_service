package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;

    public ResponseEntity<StageDto> createStage(Stage stage) {
        return null;
    }

    public ResponseEntity<List<StageDto>> getStages(Long projectId) {
        return null;
    }

    public void deleteStage(Long stageId) {

    }

    public ResponseEntity<StageUpdateDto> updateStage(Long stageId, StageDto stageUpdateDto) {
        return null;
    }

    public ResponseEntity<StageDto> sendInvitations(Long stageId, Stage stage) {
        return null;
    }

    public ResponseEntity<StageDto> getStageDetails(Long stageId) {

        return null;
    }

    public ResponseEntity<List<Task>> getStageTasks(Long stageId, TaskStatus status) {
        return null;
    }

    public ResponseEntity<StageUpdateDto> updateStageParticipants(Set<StageUpdateDto> stageUpdateDto) {
        return null;
    }
}
