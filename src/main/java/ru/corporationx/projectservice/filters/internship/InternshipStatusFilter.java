package ru.corporationx.projectservice.filters.internship;

import org.springframework.stereotype.Component;
import ru.corporationx.projectservice.model.dto.internship.InternshipFilterDto;
import ru.corporationx.projectservice.model.entity.Internship;

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
