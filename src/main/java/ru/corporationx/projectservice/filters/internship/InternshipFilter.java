package ru.corporationx.projectservice.filters.internship;

import ru.corporationx.projectservice.model.dto.internship.InternshipFilterDto;
import ru.corporationx.projectservice.model.entity.Internship;

import java.util.stream.Stream;

public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto filters);

    Stream<Internship> apply(Stream<Internship> internship, InternshipFilterDto filters);
}
