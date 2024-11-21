package faang.school.projectservice.service.momentService.filter;

import faang.school.projectservice.dto.momentDto.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto filter);

    void apply(Stream<Moment> moments, MomentFilterDto filter);
}
