package faang.school.projectservice.filter.project.meet;

import faang.school.projectservice.dto.project.meet.MeetFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Meet;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MeetTitleFilter implements Filter<MeetFilterDto, Stream<Meet>> {
    @Override
    public boolean isApplicable(MeetFilterDto meetFilterDto) {
        return meetFilterDto.getTitle() != null;
    }

    @Override
    public Stream<Meet> apply(MeetFilterDto meetFilterDto, Stream<Meet> meetStream) {
        return meetStream.filter(meet -> meet.getTitle().contains(meetFilterDto.getTitle()));
    }
}
