package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.filter.MomentFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentProjectFilter implements Filter<Moment, MomentFilterDto> {
    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return filter.getProjectIds() != null && !filter.getProjectIds().isEmpty();
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filter) {
        return moments.filter(moment -> moment.getProjects().stream()
                .anyMatch(project -> filter.getProjectIds().contains(project.getId())));
    }
}
