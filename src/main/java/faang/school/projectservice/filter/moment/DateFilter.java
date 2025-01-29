package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.time.Month;
import java.util.stream.Stream;

@Component
public class DateFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return filter.getMonthNumber() >= Month.JANUARY.getValue()
                && filter.getMonthNumber() <= Month.DECEMBER.getValue();
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> elements, MomentFilterDto filter) {
        return elements.filter(moment -> moment.getDate().getMonthValue() == filter.getMonthNumber());
    }
}
