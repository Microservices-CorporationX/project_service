package faang.school.projectservice.service.filter.Internship;

import faang.school.projectservice.dto.client.internship.InternshipFilterRequest;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public interface InternshipFilter {
    Stream<Internship> filter(Stream<Internship> stream, InternshipFilterRequest request);
}
