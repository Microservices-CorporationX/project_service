package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.InternshipCreatedDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipGetAllDto;
import faang.school.projectservice.dto.internship.InternshipGetByIdDto;
import faang.school.projectservice.dto.internship.InternshipUpdatedDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.handler.InternshipCompletionHandler;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.validator.InternshipValidator;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@Valid
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final InternshipValidator internshipValidator;
    private final ProjectValidator projectValidator;
    private final InternshipCompletionHandler completionHandler;
    private final List<Filter<TeamMember, InternshipFilterDto>> roleFilter;
    private final List<Filter<Internship, InternshipFilterDto>> statusFilter;

    public InternshipCreatedDto createInternship(InternshipCreatedDto createInternship) {
        internshipValidator.durationValidate(createInternship);
        projectValidator.validateMentorPresenceInProjectTeam(createInternship);
        return internshipMapper.toCreatedDto(internshipRepository.save(internshipMapper.createInternship(createInternship)));
    }

    @Transactional
    public InternshipUpdatedDto updateInternship(InternshipUpdatedDto updateInternship) {
        Internship internship = internshipRepository.findById(updateInternship.getId())
                .orElseThrow(() -> {
                    log.error("Internship with ID {} not found", updateInternship.getId());
                    return new EntityNotFoundException();
                });

        completionHandler.internsToDismissal(updateInternship.getInterns());
        completionHandler.processInternshipCompletion(internship, internship.getStatus());

        Internship savedInternship = internshipRepository.save(internshipMapper.toEntity(updateInternship));
        return internshipMapper.toUpdatedDto(savedInternship);
    }

    public List<InternshipFilterDto> filterInternship(InternshipFilterDto filters) {
        List<Internship> allInternships = internshipRepository.findAll();

        List<Internship> filteredInternships = filterByStatus(allInternships, filters);
        List<TeamMember> filteredTeamMembers = filterByRole(allInternships, filters);

        List<InternshipFilterDto> result = new ArrayList<>();

        filteredInternships.forEach(internship -> result.add(internshipMapper.toFilterDto(internship)));
        filteredTeamMembers.forEach(teamMember -> result.add(internshipMapper.toFilterDto(teamMember)));

        return result;
    }

    public List<InternshipGetAllDto> getAllInternships() {
        List<Internship> allInternships = internshipRepository.findAll();
        List<InternshipGetAllDto> internshipGetAllDtos = new ArrayList<>();

        allInternships.forEach(internship -> internshipGetAllDtos.add(internshipMapper.toGetAllDto(internship)));

        return internshipGetAllDtos;
    }

    public InternshipGetByIdDto getByIdInternship(long internshipId) {
       Internship internship =  internshipRepository.findById(internshipId)
               .orElseThrow(() -> new EntityNotFoundException("InternShip not found"));
       return internshipMapper.toGetByIdDto(internship);
    }


    private List<Internship> filterByStatus(List<Internship> internships, InternshipFilterDto filters) {
        return statusFilter.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(internships.stream(), filters))
                .toList();
    }

    private List<TeamMember> filterByRole(List<Internship> internships, InternshipFilterDto filters) {
        Stream<TeamMember> teamMembers = internships.stream()
                .flatMap(internship -> internship.getInterns().stream());
        return roleFilter.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(teamMembers, filters))
                .toList();
    }
}
