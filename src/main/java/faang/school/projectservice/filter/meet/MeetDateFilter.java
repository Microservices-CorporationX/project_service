package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Meet;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MeetDateFilter implements Filter<Meet, MeetDto> {
    @Override
    public boolean isApplicable(MeetDto filter) {
        return filter.getCreatedAt() != null && filter.getUpdatedAt() != null;
    }

    @Override
    public Stream<Meet> apply(Stream<Meet> dataStream, MeetDto filter) {
        return dataStream.filter(meet -> meet.getCreatedAt()
                .isAfter(filter.getCreatedAt()) && meet.getCreatedAt().isBefore(filter.getUpdatedAt()));
    }
}
