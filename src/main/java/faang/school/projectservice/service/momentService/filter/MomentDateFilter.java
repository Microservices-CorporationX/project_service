package faang.school.projectservice.service.momentService.filter;

import faang.school.projectservice.dto.momentDto.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import java.util.stream.Stream;

public class MomentDateFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return filter.getDate() != null;
    }

    @Override
    public void apply(Stream<Moment> moments, MomentFilterDto filter) {
        moments.filter(moment -> moment.getDate().equals(filter.getDate()));
    }
}
