package ru.corporationx.projectservice.filters.moment;

import ru.corporationx.projectservice.filters.Filter;
import org.springframework.stereotype.Component;
import ru.corporationx.projectservice.model.dto.filter.MomentFilterDto;
import ru.corporationx.projectservice.model.entity.Moment;

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
