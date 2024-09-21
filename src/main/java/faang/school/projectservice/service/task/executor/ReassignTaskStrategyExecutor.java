package faang.school.projectservice.service.task.executor;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.strategy.DeleteStageTaskStrategy;
import faang.school.projectservice.service.stage.executor.DeleteStageStrategyExecutor;
import faang.school.projectservice.repository.StageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReassignTaskStrategyExecutor extends DeleteStageStrategyExecutor {
    private final StageRepository stageRepository;

    @Override
    public void execute(Long providerStageId, Long consumerStageId) {
        Stage consumerStage = stageRepository.getById(consumerStageId);
        if (consumerStage == null) {
            throw new IllegalArgumentException("Consumer Stage not found by id: " + consumerStageId);
        }
        stageRepository.reassignTasksFromToStage(providerStageId, consumerStage);
    }

    @Override
    public DeleteStageTaskStrategy getMethod() {
        return DeleteStageTaskStrategy.REASSIGN;
    }
}
