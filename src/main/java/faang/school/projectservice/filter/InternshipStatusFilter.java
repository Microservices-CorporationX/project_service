package faang.school.projectservice.filter;

import faang.school.projectservice.dto.internShip.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InternshipStatusFilter implements Filter<Internship, InternshipFilterDto> {
    @Override
    public boolean isApplicable(InternshipFilterDto filter) {
        return filter.getInternshipStatus() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> dataStream, InternshipFilterDto filter) {
        return dataStream.filter(internship -> internship.getStatus().equals(filter.getInternshipStatus()));
    }
}
