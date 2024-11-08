package faang.school.projectservice.filter.momentFilter;

import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectPartnerFilter implements Filter<Moment, MomentFilterDto> {
    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return filter.getProjectIds() != null && !filter.getProjectIds().isEmpty();
    }

    public Stream<Moment> applay(Stream<Moment> moments, MomentFilterDto filter) {
        return moments.filter(moment -> moment.getProjects().stream()
                .anyMatch(project -> filter.getProjectIds().contains(project.getId())));
    }
}
