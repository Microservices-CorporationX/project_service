package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class MomentFilterToFromDate implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filterDto) {
        return filterDto.getFromDate() != null && filterDto.getToDate() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filterDto) {
        return moments.filter(moment ->
                moment.getDate().isAfter(filterDto.getFromDate()) && moment.getDate().isBefore(filterDto.getToDate()));
    }
}
