package faang.school.projectservice.service.internship.filter;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InternshipStatusFilter implements InternshipFilter {

    private final InternshipMapper internshipMapper;

    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getStatusPattern() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internships, InternshipFilterDto filters) {
        return internships.filter(internship -> internshipMapper.toDto(internship).status().equals(filters.getStatusPattern()));
    }
}
