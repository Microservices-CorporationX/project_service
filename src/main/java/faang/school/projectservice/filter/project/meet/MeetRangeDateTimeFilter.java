package faang.school.projectservice.filter.project.meet;

import faang.school.projectservice.dto.project.meet.MeetFilterDto;
import faang.school.projectservice.dto.project.meet.util.RangeDateTime;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Meet;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class MeetRangeDateTimeFilter implements Filter<MeetFilterDto, Stream<Meet>> {
    @Override
    public boolean isApplicable(MeetFilterDto meetFilterDto) {
        return meetFilterDto.getRangeDateTime() != null;
    }

    @Override
    public Stream<Meet> apply(MeetFilterDto meetFilterDto, Stream<Meet> meetStream) {
        return meetStream.filter(meet -> rangeDateTime(meetFilterDto.getRangeDateTime(), meet.getStartDateTime()));
    }

    private boolean rangeDateTime(RangeDateTime range, LocalDateTime date) {
        return !date.isBefore(range.getStart()) && !date.isAfter(range.getEnd());
    }
}
