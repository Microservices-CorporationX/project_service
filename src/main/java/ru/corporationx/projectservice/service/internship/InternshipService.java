package ru.corporationx.projectservice.service.internship;

import ru.corporationx.projectservice.exception.DataValidationException;
import ru.corporationx.projectservice.filters.internship.InternshipFilter;
import ru.corporationx.projectservice.mapper.internship.InternshipMapper;
import ru.corporationx.projectservice.model.dto.internship.InternshipDto;
import ru.corporationx.projectservice.model.dto.internship.InternshipFilterDto;
import ru.corporationx.projectservice.model.entity.Internship;
import ru.corporationx.projectservice.model.entity.TeamMember;
import ru.corporationx.projectservice.repository.InternshipRepository;
import ru.corporationx.projectservice.repository.TeamMemberRepository;
import ru.corporationx.projectservice.validator.internship.InternshipValidator;
import ru.corporationx.projectservice.validator.internship.TaskStatusValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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
        List<TeamMember> interns = teamMemberRepository.findAllByIdIn(internshipDto.getInternIds())
                .orElse(Collections.emptyList());
        internshipToSave.setInterns(interns);

        internshipRepository.save(internshipToSave);
        log.info("The internship called {}, with ID {} was created",
                internshipDto.getName(),
                internshipToSave.getId());
        return internshipMapper.toDto(internshipToSave);
    }

    public InternshipDto updateInternship(InternshipDto internshipDto) {
        internshipValidator.validateInternshipTotalDuration(internshipDto);

        Internship internship = internshipRepository.findById(internshipDto.getId())
                .orElseThrow(() -> new DataValidationException(String.format("Internship doesn't exist by id: %s", internshipDto.getId())));
        List<TeamMember> internsBeforeUpdate = teamMemberRepository.findAllByIdIn(internship.getInterns().stream()
                .map(TeamMember::getId)
                .toList())
                .orElse(Collections.emptyList());
        if (internshipValidator.validateInternsNotAddedAfterStart(internship, internsBeforeUpdate, internshipDto)) {
            internship = internshipMapper.update(internshipDto, internship);
        }

        Internship internshipAfterUpdate = taskStatusValidator.checkingInternsTaskStatus(internship);

        internshipRepository.save(internshipAfterUpdate);
        log.info("The internship called {}, with ID {} was updated", internshipAfterUpdate.getName(), internship.getId());
        return internshipMapper.toDto(internshipAfterUpdate);
    }

    public List<InternshipDto> getInternships() {
        List<Internship> allInternships = internshipRepository.findAll();
        log.info("The request for all internships was successful");
        return internshipMapper.mapToDtoList(allInternships);
    }

    public List<InternshipDto> getInternships(InternshipFilterDto filters) {
        Stream<Internship> internships = new ArrayList<>(internshipRepository.findAll()).stream();
        log.info("The request for all internships using filters was successful");
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
        log.info("The request for an internship by ID {} was successful", internshipId);
        return internshipMapper.toDto(internship);
    }
}
