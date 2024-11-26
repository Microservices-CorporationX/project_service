package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipStatusDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.service.TeamService;
import faang.school.projectservice.service.internship.filter.InternshipFilter;
import faang.school.projectservice.validator.InternshipServiceValidator;
import java.util.ArrayList;
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
    private final List<InternshipFilter> internshipFilterList;

    @Override
    public InternshipDto create(InternshipDto internshipDto) {
        validator.validateInternshipDuration(internshipDto);
        validator.validateMentor(internshipDto);
        validator.validateMembersRoles(internshipDto);

        Project project = projectService.getProjectById(internshipDto.ownedProjectId());
        List<TeamMember> internsList = internshipDto.internIds().stream().map(teamMemberService::findById).toList();
        TeamMember mentor = teamMemberService.findById(internshipDto.mentorId());

        Team internsTeam = teamService.createTeam(internsList, project);
        projectService.saveNewTeam(internsTeam, internshipDto.ownedProjectId());

        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setInterns(internsList);
        internship.setMentorId(mentor);
        internship.setProject(project);

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    @Override
    public InternshipDto update(InternshipDto internshipDto) {
        validator.validateInternshipDuration(internshipDto);
        validator.validateMentor(internshipDto);
        validator.validateMembersRoles(internshipDto);
        validator.validateCountOfInterns(internshipDto);

        if(internshipDto.status() == InternshipStatusDto.COMPLETED) {
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
        Project project = projectService.getProjectById(internshipDto.ownedProjectId());
        Internship internship = internshipRepository.getReferenceById(internshipDto.id());

        internshipDto.internIds().forEach(internId -> {
            TeamMember teamMember = teamMemberService.findById(internId);
            boolean isInternshipSuccessful =  project.getTasks().stream()
                    .filter(task -> task.getPerformerUserId().equals(internId))
                    .allMatch(task -> task.getStatus() == TaskStatus.DONE || task.getStatus() == TaskStatus.CANCELLED);

            if(isInternshipSuccessful) {
                List<TeamRole> teamRoles = new ArrayList<>(teamMember.getRoles());
                TeamRole role = internshipDto.role().toTeamRole();
                teamRoles.add(role);
                teamMember.setRoles(teamRoles);
                teamMemberService.save(teamMember);
            } else {
                List<Team> projectTeamsList = new ArrayList<>(project.getTeams());
                Team projectTeam = projectTeamsList.stream()
                        .filter(team -> team.getTeamMembers().contains(teamMember))
                        .findAny().orElseThrow(() -> new DataValidationException(
                                "Intern with id " + internId + "does not exists in project"));
                List<TeamMember> teamMembers = new ArrayList<>(projectTeam.getTeamMembers());
                teamMembers.remove(teamMember);
                projectTeam.setTeamMembers(teamMembers);
                int index = projectTeamsList.indexOf(projectTeam);
                projectTeamsList.set(index, projectTeam);
                project.setTeams(projectTeamsList);
                projectService.saveProject(project);

                List<TeamMember> internsList = new ArrayList<>(internship.getInterns());
                internsList.remove(teamMember);
                internship.setInterns(internsList);
                internshipRepository.save(internship);
            }
        });
    }
}
