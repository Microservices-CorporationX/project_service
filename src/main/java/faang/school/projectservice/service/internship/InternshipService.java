package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.intership.InternshipFilterDto;
import faang.school.projectservice.dto.intership.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filters.internship.InternshipFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipMapper internshipMapper;
    private final InternshipValidator internshipValidator;
    private final TaskStatusValidator taskStatusValidator;
    private final List<InternshipFilter> internshipFilters;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        internshipValidator.validate(internshipDto);
        internshipValidator.validateInternshipTotalDuration(internshipDto);

        Internship internshipToSave = internshipMapper.toEntity(internshipDto);
        List<TeamMember> interns = teamMemberRepository.findAllById(internshipDto.getInternIds());
        internshipToSave.setInterns(interns);

        internshipRepository.save(internshipToSave);
        log.info("The internship called {} was successfully created!", internshipDto.getName());
        return internshipMapper.toDto(internshipToSave);
    }

    public InternshipDto updateInternship(InternshipDto internshipDto) {
        internshipValidator.validateInternshipTotalDuration(internshipDto);

        Internship internship = internshipRepository.findById(internshipDto.getId())
                .orElseThrow(() -> new DataValidationException(String.format("Internship doesn't exist by id: %s", internshipDto.getId())));
        List<TeamMember> internsBeforeUpdate = teamMemberRepository.findAllById(internship.getInterns().stream()
                .map(TeamMember::getId)
                .toList());
        if (internshipValidator.validateInternsNotAddedAfterStart(internship, internsBeforeUpdate, internshipDto)) {
            internship = internshipMapper.update(internshipDto, internship);
        }

        Internship internshipAfterUpdate = taskStatusValidator.checkingInternsTaskStatus(internship);

        internshipRepository.save(internshipAfterUpdate);
        log.info("The internship called {} was successfully updated!", internshipAfterUpdate.getName());
        return internshipMapper.toDto(internshipAfterUpdate);
    }

    public List<InternshipDto> getInternships() {
        List<Internship> allInternships = internshipRepository.findAll();
        log.info("Request for getting all internships");
        return internshipMapper.mapToDtoList(allInternships);
    }

    public List<InternshipDto> getInternships(InternshipFilterDto filters) {
        Stream<Internship> internships = new ArrayList<>(internshipRepository.findAll()).stream();
        log.info("Request to get all internships using filters");
        return internshipFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(internships,
                        (stream, filter) -> filter.apply(stream, filters),
                        (s1, s2) -> s1)
                .map(internshipMapper::toDto)
                .toList();
    }

    public InternshipDto getInternship(Long internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new DataValidationException(String.format("Internship doesn't exist by id: %s", internshipId)));
        log.info("Request for an internship by ID: {}", internshipId);
        return internshipMapper.toDto(internship);
    }
}
