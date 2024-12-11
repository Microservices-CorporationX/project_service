package faang.school.projectservice.filters.moment;

import faang.school.projectservice.dto.filter.MomentFilterDto;
import faang.school.projectservice.filters.Filter;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentMonthFilter implements Filter<Moment, MomentFilterDto> {
    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return filter.getMonthPattern() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filter) {
        return moments.filter(moment -> moment.getDate().getMonth().equals(filter.getMonthPattern()));
    }
}
