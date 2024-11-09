package faang.school.projectservice.service.filter.moment;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto filters);

    Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters);
}
