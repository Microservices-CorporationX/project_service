package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.MomentFilter;

import java.util.stream.Stream;

public class MomentProjectsFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return !filter.projectsIds().isEmpty();
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filter) {
        return moments.filter(moment -> filter.projectsIds().contains(moment.getProjects().stream()
                .map(Project::getId).toList())).toList().stream();
    }
}
