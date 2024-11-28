package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipStatusDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.TaskDto;
import faang.school.projectservice.dto.project.TaskStatusDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.service.TeamService;
import faang.school.projectservice.service.internship.filter.InternshipFilter;
import faang.school.projectservice.validator.InternshipServiceValidator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipServiceValidator validator;
    private final InternshipMapper internshipMapper;
    private final TeamMemberService teamMemberService;
    private final TeamService teamService;
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final List<InternshipFilter> internshipFilterList;

    @Override
    public InternshipDto create(InternshipDto internshipDto) {
        validator.validateInternshipDuration(internshipDto);
        validator.validateMentor(internshipDto);
        validator.validateMembersRoles(internshipDto);

        ProjectDto projectDto = projectService.getProjectById(internshipDto.ownedProjectId());
        List<TeamMember> internsList = internshipDto.internIds().stream().map(teamMemberService::findById).toList();
        TeamMember mentor = teamMemberService.findById(internshipDto.mentorId());

        Team internsTeam = teamService.createTeam(internsList, projectMapper.toEntity(projectDto));
        projectService.saveNewTeam(internsTeam, internshipDto.ownedProjectId());

        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setInterns(internsList);
        internship.setMentorId(mentor);
        internship.setProject(projectMapper.toEntity(projectDto));

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    @Override
    public InternshipDto update(InternshipDto internshipDto) {
        validator.validateInternshipDuration(internshipDto);
        validator.validateMentor(internshipDto);
        validator.validateMembersRoles(internshipDto);
        validator.validateCountOfInterns(internshipDto);

        if (internshipDto.status() == InternshipStatusDto.COMPLETED) {
            completeInternship(internshipDto);
        }

        return internshipDto;
    }

    @Override
    public List<InternshipDto> getFilteredInternships(InternshipFilterDto filters) {
        List<Internship> allInternships = internshipRepository.findAll();
        Stream<Internship> internships = allInternships.stream();

        return internshipFilterList.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(internships, filters))
                .map(internshipMapper::toDto)
                .toList();
    }

    @Override
    public List<InternshipDto> getAllInternships() {
        return internshipRepository.findAll().stream().map(internshipMapper::toDto).toList();
    }

    @Override
    public Optional<InternshipDto> getInternshipById(Long id) {
        return internshipRepository.findById(id).map(internshipMapper::toDto);
    }

    private void completeInternship(InternshipDto internshipDto) {
        List<TaskDto> taskDtoList = projectService.getProjectTasks(internshipDto.ownedProjectId());

        internshipDto.internIds().forEach(internId -> {
            boolean isInternshipSuccessful = taskDtoList.stream()
                    .filter(taskDto -> taskDto.performerUserId().equals(internId))
                    .allMatch(taskDto -> taskDto.status() == TaskStatusDto.DONE
                            || taskDto.status() == TaskStatusDto.CANCELLED);

            if (isInternshipSuccessful) {
                teamMemberService.addNewRole(internId, internshipDto.role());
            } else {
                projectService.deleteTeamMember(internshipDto.ownedProjectId(), internId);
                deleteIntern(internshipDto.id(), internId);
            }
        });
    }

    private void deleteIntern(Long internshipId, Long internId) {
        Internship internship = internshipRepository.getReferenceById(internshipId);
        List<TeamMember> filteredInternsList = internship.getInterns().stream()
                .filter(intern -> !intern.getId().equals(internId))
                .toList();
        internship.setInterns(filteredInternsList);
        internshipRepository.save(internship);
    }
}
