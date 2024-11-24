package faang.school.projectservice.filters.internship;

import faang.school.projectservice.dto.intership.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto filters);

    Stream<Internship> apply(Stream<Internship> internship, InternshipFilterDto filters);
}
