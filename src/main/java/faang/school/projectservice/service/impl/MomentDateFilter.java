package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.service.MomentFilter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class MomentDateFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        LocalDateTime dateFrom = getDefaultDateFrom(filter.dateFrom());
        LocalDateTime dateTo = getDefaultDateTo(filter.dateTo());
        return !dateFrom.isAfter(dateTo) && !dateFrom.isEqual(dateTo);
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filter) {
        LocalDateTime dateFrom = getDefaultDateFrom(filter.dateFrom());
        LocalDateTime dateTo = getDefaultDateTo(filter.dateTo());
        return moments
                .filter(moment -> moment.getDate().isAfter(dateFrom) && moment.getDate().isBefore(dateTo))
                .toList()
                .stream();
    }

    private LocalDateTime getDefaultDateFrom(LocalDateTime dateFrom) {
        if (dateFrom == null) {
            dateFrom = LocalDateTime.MIN;
        }
        return dateFrom;
    }

    private LocalDateTime getDefaultDateTo(LocalDateTime dateFrom) {
        if (dateFrom == null) {
            dateFrom = LocalDateTime.MAX;
        }
        return dateFrom;
    }
}
