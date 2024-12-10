package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.validator.team_member.TeamMemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final TeamMemberValidator teamMemberValidator;

    public void createTask(CreateTaskDto taskDto, long teamMemberId) {
        //validation is team member exists
        TeamMember taskCreator = teamMemberService.findById(teamMemberId);
        Project project = projectService.getProjectById(taskDto.getProjectId());
        //check is user participant of project
        teamMemberValidator.validateIsTeamMemberParticipantOfProject(taskCreator, project);

        taskRepository.save(taskMapper.toEntity(taskDto));
    }
}
