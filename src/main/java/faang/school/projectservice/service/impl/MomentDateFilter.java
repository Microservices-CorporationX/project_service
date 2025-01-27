package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.service.MomentFilter;
import faang.school.projectservice.utils.Constants;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Stream;

import static java.time.LocalDateTime.parse;

@Component
public class MomentDateFilter implements MomentFilter {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT, Locale.ENGLISH);

    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        if (StringUtils.isBlank(filter.dateFrom()) || StringUtils.isBlank(filter.dateTo())) {
            return false;
        }
        LocalDateTime dateFrom;
        LocalDateTime dateTo;

        try {
            dateFrom = parse(filter.dateFrom(), formatter);
            dateTo = parse(filter.dateTo(), formatter);
            if (dateFrom.isAfter(dateTo) || dateFrom.isEqual(dateTo)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filter) {
        LocalDateTime dateFrom = parse(filter.dateFrom(), formatter);
        LocalDateTime dateTo = parse(filter.dateTo(), formatter);
        return moments
                .filter(moment -> moment.getDate().isAfter(dateFrom) && moment.getDate().isBefore(dateTo))
                .toList()
                .stream();
    }
}
