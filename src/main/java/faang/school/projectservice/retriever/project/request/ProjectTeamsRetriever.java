package faang.school.projectservice.retriever.project.request;

import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectTeamsRetriever implements ProjectRetriever {
    private final TeamService teamService;

    @Override
    public void retrieveData(Project project, ProjectRequestDto projectRequestDto) {
        project.setTeams(teamService.getTeamsByIds(projectRequestDto.getTeamsIds()));
    }
}
