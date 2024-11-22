package faang.school.projectservice.service.internship.filter;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import java.util.stream.Stream;

public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto filters);
    Stream<Internship> apply(Stream<Internship> internships, InternshipFilterDto filters);
}
