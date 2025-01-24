package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipCreateDto;
import faang.school.projectservice.dto.internship.InternshipEditDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.internship.InternshipCreateMapper;
import faang.school.projectservice.mapper.internship.InternshipEditMapper;
import faang.school.projectservice.mapper.internship.InternshipReadMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
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
    private final TeamRepository teamRepository;
    private final InternshipRepository internshipRepository;
    private final InternshipCreateMapper internshipCreateMapper;
    private final InternshipEditMapper internshipEditMapper;
    private final InternshipReadMapper internshipReadMapper;
    private final InternshipValidator internshipValidator;

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

        Internship internship = internshipRepository.findById(internshipDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        List<TeamMember> interns = new ArrayList<>();
        TeamMember intern;

        for (Long id : internshipDto.getInternsIds()) {
            if (internshipValidator.validateInternCompletedInternship(internshipDto, id)) {
                intern = teamMemberRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

                intern.getRoles().add(internshipDto.getTargetRole());
                interns.add(intern);
            }
        }

        internship.setInterns(interns);
        internshipRepository.save(internship);

        return internshipDto;
    }

    public List<InternshipCreateDto> getInternshipsByFilters() {
        return null;
    }

    public List<InternshipCreateDto> getInternships() {
        return null;
    }

    public InternshipCreateDto getInternshipById(long internshipId) {
        return null;
    }

    private List<TeamMember> getInternsById(List<Long> internsIds) {
        List<TeamMember> interns = new ArrayList<>();

        for (Long id : internsIds) {
            interns.add(teamMemberRepository.findById(id).get());
        }

        return interns;
    }
}
