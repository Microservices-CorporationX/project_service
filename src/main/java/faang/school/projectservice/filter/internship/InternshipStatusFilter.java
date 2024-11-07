package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public class InternshipStatusFilter implements InternshipFilter {

    @Override
    public boolean isApplicable(InternshipFilterDto filterDto) {
        return filterDto != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internships, InternshipFilterDto filterDto) {
        return internships.filter(internship -> internship.getStatus() == filterDto.getInternshipStatus());
    }
}
