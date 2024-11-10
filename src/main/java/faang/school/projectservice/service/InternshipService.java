package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.internShip.InternshipCreatedDto;
import faang.school.projectservice.dto.client.internShip.InternshipFilterDto;
import faang.school.projectservice.dto.client.internShip.InternshipGetAllDto;
import faang.school.projectservice.dto.client.internShip.InternshipGetByIdDto;
import faang.school.projectservice.dto.client.internShip.InternshipUpdatedDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.handler.InternshipCompletionHandler;
import faang.school.projectservice.mapper.InternshipCreateMapper;
import faang.school.projectservice.mapper.InternshipFilterMapper;
import faang.school.projectservice.mapper.InternshipGetAllMapper;
import faang.school.projectservice.mapper.InternshipGetByIdMapper;
import faang.school.projectservice.mapper.InternshipUpdateMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.validator.InternshipDurationValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final ProjectService projectService;
    private final InternshipRepository internshipRepository;
    private final InternshipCreateMapper internshipCreateMapper;
    private final InternshipUpdateMapper internshipUpdateMapper;
    private final InternshipGetAllMapper internshipGetAllMapper;
    private final InternshipFilterMapper internshipFilterMapper;
    private final InternshipGetByIdMapper internshipGetByIdMapper;
    private final InternshipDurationValidator internshipDurationValidator;
    private final InternshipCompletionHandler completionHandler;
    private final List<Filter<TeamMember, InternshipFilterDto>> roleFilter;
    private final List<Filter<Internship, InternshipFilterDto>> statusFilter;


    public InternshipCreatedDto createInternship(InternshipCreatedDto internShipCreatedDto) {
        internshipDurationValidator.durationValidate(internShipCreatedDto);
        projectService.getProjectTeamMembersIds(internShipCreatedDto);
        return internshipCreateMapper.toDto(internshipRepository.save(internshipCreateMapper.toEntity(internShipCreatedDto)));
    }

    public InternshipUpdatedDto updateInternship(InternshipUpdatedDto internShipUpdatedDto) {
        Internship internship = internshipRepository.findById(internShipUpdatedDto.getId())
                .orElseThrow(EntityNotFoundException::new);

        completionHandler.internsToDismissal(internShipUpdatedDto.getInternToDismissal());

        if (internShipUpdatedDto.getStatus() == InternshipStatus.COMPLETED) {
            completionHandler.handleInternsCompletion(internship);
        }

        Internship savedInternship = internshipRepository.save(internshipUpdateMapper.toEntity(internShipUpdatedDto));
        return internshipUpdateMapper.toDto(savedInternship);
    }

    public List<InternshipFilterDto> filterInternship(InternshipFilterDto filters) {
        List<Internship> filteredInternships = filterByStatus(filters);
        List<TeamMember> filteredTeamMembers = filterByRole(filters);

        List<InternshipFilterDto> result = new ArrayList<>();

        filteredInternships.forEach(internship -> result.add(internshipFilterMapper.toDto(internship)));
        filteredTeamMembers.forEach(teamMember -> result.add(internshipFilterMapper.toDto(teamMember)));

        return result;
    }

    public List<InternshipGetAllDto> getAllInternships() {
        List<Internship> allInternships = internshipRepository.findAll();
        List<InternshipGetAllDto> internshipGetAllDtos = new ArrayList<>();

        allInternships.forEach(internship -> internshipGetAllDtos.add(internshipGetAllMapper.toDto(internship)));

        return internshipGetAllDtos;
    }

    public InternshipGetByIdDto getByIdInternship(long internShipId) {
       Internship internship =  internshipRepository.findById(internShipId)
               .orElseThrow(() -> new EntityNotFoundException("InternShip not found"));
       return internshipGetByIdMapper.toDto(internship);
    }


    private List<Internship> filterByStatus(InternshipFilterDto filters) {
        Stream<Internship> internship = internshipRepository.findAll().stream();
        return statusFilter.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(internship, filters))
                .toList();
    }


    private List<TeamMember> filterByRole(InternshipFilterDto filters) {
        List<Internship> interns = internshipRepository.findAll();
        Stream<TeamMember> teamMembers = interns.stream().flatMap(member -> member.getInterns().stream());
        return roleFilter.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(teamMembers, filters))
                .toList();
    }
}
