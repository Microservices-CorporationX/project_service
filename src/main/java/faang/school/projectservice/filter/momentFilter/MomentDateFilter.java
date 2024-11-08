package faang.school.projectservice.filter.momentFilter;

import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentDateFilter implements Filter<Moment, MomentFilterDto> {
    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return filter.getMonth() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filter) {
        return moments.filter(moment -> moment.getCreatedAt().getMonth().equals(filter.getMonth()));
    }
}
