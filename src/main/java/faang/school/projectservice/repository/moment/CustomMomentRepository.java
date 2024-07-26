package faang.school.projectservice.repository.moment;

import faang.school.projectservice.model.Moment;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomMomentRepository {
    List<Moment> findAllByProjectIdAndDateBetween(Long projectId, LocalDateTime start, LocalDateTime endExclusive);
}
