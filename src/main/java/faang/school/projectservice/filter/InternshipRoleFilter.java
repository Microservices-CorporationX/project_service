package faang.school.projectservice.filter;

import faang.school.projectservice.dto.internShip.InternshipFilterDto;
import faang.school.projectservice.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InternshipRoleFilter implements Filter <TeamMember, InternshipFilterDto> {
    @Override
    public boolean isApplicable(InternshipFilterDto filter) {
        return filter.getTeamRole() != null;
    }

    @Override
    public Stream<TeamMember> apply(Stream<TeamMember> dataStream, InternshipFilterDto filter) {
        return dataStream.filter(teamMember ->
                teamMember.getRoles().contains(filter.getTeamRole())
        );
    }
}
