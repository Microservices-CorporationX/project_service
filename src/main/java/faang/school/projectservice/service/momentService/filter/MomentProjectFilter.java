package faang.school.projectservice.service.momentService.filter;

import faang.school.projectservice.dto.momentDto.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import java.util.stream.Stream;

public class MomentProjectFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return filter.getProjectIds() != null && !filter.getProjectIds().isEmpty();
    }

    @Override
    public void apply(Stream<Moment> moments, MomentFilterDto filter) {
        moments.filter(moment ->
                moment.getProjects().stream()
                    .anyMatch(project -> filter.getProjectIds().contains(project.getId()))
        );
    }
}
