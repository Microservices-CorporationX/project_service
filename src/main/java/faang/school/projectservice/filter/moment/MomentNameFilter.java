package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentNameFilter extends MomentFilter {
    @Override
    public Object getFilterFieldValue(MomentFilterDto filters) {
        return filters.getNamePattern();
    }

    @Override
    public boolean apply(Moment moment, MomentFilterDto filters) {
        return moment.getName().contains(filters.getNamePattern());
    }
}
