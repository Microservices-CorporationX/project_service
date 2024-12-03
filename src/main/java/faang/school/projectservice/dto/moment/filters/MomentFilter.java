package faang.school.projectservice.dto.moment.filters;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto filter);

    Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filter);
}
