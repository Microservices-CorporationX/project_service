package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MomentProjectIdsFilter extends MomentFilter {
    @Override
    public Object getFilterFieldValue(MomentFilterDto filters) {
        return filters.getProjectIdsPattern();
    }

    @Override
    public boolean apply(Moment moment, MomentFilterDto filters) {
        Set<Long> projectIds = moment.getProjects().stream()
                .map(Project::getId)
                .collect(Collectors.toSet());

        return projectIds.containsAll(filters.getProjectIdsPattern());
    }
}
