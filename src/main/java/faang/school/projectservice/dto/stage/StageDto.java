package faang.school.projectservice.dto.stage;

import faang.school.projectservice.dto.team_member.TeamMemberDto;
import faang.school.projectservice.model.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class StageDto {
    private Long stageId;
    private String stageName;
    private Project project;
    private List<TeamMemberDto> executors;
}
