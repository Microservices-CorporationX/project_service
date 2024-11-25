package faang.school.projectservice.filters.internship;

import faang.school.projectservice.dto.intership.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InternshipStatusFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getStatusPattern() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internships, InternshipFilterDto filters) {
        return internships.filter(internship -> internship.getStatus() == filters.getStatusPattern());
    }
}
