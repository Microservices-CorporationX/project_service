package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

public class StageDeleteFilter implements StageFilter{
    @Override
    public boolean isApplicable(StageFilterDto filters) {
        return false;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto filters) {
        return Stream.empty();
    }
}
