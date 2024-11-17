package faang.school.projectservice.filters.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto momentFilterDto);

    Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto momentFilterDto);
}
