package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.service.MomentFilter;

import java.util.stream.Stream;

public class MomentProjectsFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return false;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filter) {
        return null;
    }
}
