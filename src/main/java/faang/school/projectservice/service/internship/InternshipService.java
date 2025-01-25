package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipCreateDto;
import faang.school.projectservice.dto.internship.InternshipEditDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipReadDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipCreateMapper;
import faang.school.projectservice.mapper.internship.InternshipEditMapper;
import faang.school.projectservice.mapper.internship.InternshipReadMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.internship.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private static final String ENTITY_NOT_FOUND = "Сущность не найдена";

    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipRepository internshipRepository;
    private final InternshipCreateMapper internshipCreateMapper;
    private final InternshipEditMapper internshipEditMapper;
    private final InternshipReadMapper internshipReadMapper;
    private final InternshipValidator internshipValidator;
    private final List<InternshipFilter> filters;

    public InternshipCreateDto createInternship(InternshipCreateDto internshipDto) {
        internshipValidator.validateInternshipCreation(internshipDto);
        Internship internship = internshipCreateMapper.toEntity(internshipDto);

        internship.setProject(projectRepository.findById(internshipDto.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND)));
        internship.setMentorId(teamMemberRepository.findById(internshipDto.getMentorId()).get());
        internship.setInterns(getInternsById(internshipDto.getInternsIds()));

        internshipRepository.save(internship);

        return internshipCreateMapper.toDto(internship);
    }

    public InternshipEditDto updateInternship(InternshipEditDto internshipDto) {
        internshipValidator.validateInternshipUpdating(internshipDto);

        Internship internship = findInternshipById(internshipDto.getId());
        List<TeamMember> interns = new ArrayList<>();
        TeamMember intern;

        for (Long id : internshipDto.getInternsIds()) {
            if (internshipValidator.validateInternCompletedInternship(internshipDto, id)) {
                intern = teamMemberRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

                intern.getRoles().add(internshipDto.getRole());
                interns.add(intern);
            }
        }

        internship.setInterns(interns);
        Internship updatedInternship = internshipRepository.save(internship);

        return internshipEditMapper.toDto(updatedInternship);
    }

    public List<InternshipReadDto> getInternshipsByFilters(InternshipFilterDto internshipDto) {
        List<Internship> internships = findInternships();
        List<InternshipFilter> applicableFilters = filters.stream()
                .filter(internshipFilter -> internshipFilter.isApplicable(internshipDto))
                .toList();

        if (applicableFilters.isEmpty()) {
            return List.of();
        }

        return applicableFilters.stream()
                .reduce(internships.stream(),
                        (internshipStream, internshipFilter) -> internshipFilter.apply(internshipStream, internshipDto),
                        (list1, list2) -> list1)
                .map(internshipReadMapper::toDto)
                .toList();
    }

    public List<Internship> findInternships() {
        return internshipRepository.findAll();
    }

    public List<InternshipReadDto> getInternships() {
        return findInternships().stream()
                .map(internshipReadMapper::toDto)
                .toList();
    }

    public Internship findInternshipById(long internshipId) {
        return internshipRepository.findById(internshipId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
    }

    public InternshipReadDto getInternshipById(long internshipId) {
        return internshipReadMapper.toDto(findInternshipById(internshipId));
    }

    private List<TeamMember> getInternsById(List<Long> internsIds) {
        List<TeamMember> interns = new ArrayList<>();

        for (Long id : internsIds) {
            interns.add(teamMemberRepository.findById(id).get());
        }

        return interns;
    }
}
