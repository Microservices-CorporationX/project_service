package faang.school.projectservice.dto.moment.filters;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentStartDateFromFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return filter.getCreatedAt() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filter) {
        return moments.filter(moment -> moment.getCreatedAt().isAfter(filter.getCreatedAt()));
    }
}
