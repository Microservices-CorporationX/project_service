package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import java.util.List;
import java.util.Optional;

public interface InternshipService {
    InternshipDto create(InternshipDto internshipDto);

    InternshipDto update(InternshipDto internshipDto);

    List<InternshipDto> getFilteredInternships(InternshipFilterDto filters);

    List<InternshipDto> getAllInternships();

    Optional<InternshipDto> getInternshipById(Long id);


}
