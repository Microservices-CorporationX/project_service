package faang.school.projectservice.model.stage.deletestrategy;

import faang.school.projectservice.dto.stage.DeleteTypeDto;
import faang.school.projectservice.model.stage.DeleteStrategy;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class DeleteStrategyExecutor {
    protected final StageRepository stageRepository;
    @Getter
    private final DeleteStrategy strategyType;

    public abstract void execute(Stage stage, DeleteTypeDto deleteTypeDto);
}