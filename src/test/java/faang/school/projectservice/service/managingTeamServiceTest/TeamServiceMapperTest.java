package faang.school.projectservice.service.managingTeamServiceTest;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.mapper.managingTeamMapper.ManagingTeamMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TeamServiceMapperTest {

    private final ManagingTeamMapper mapper = Mappers.getMapper(ManagingTeamMapper.class);

    @Test
    void shouldMapTeamMemberToDtoCorrectly() {
        Team team = new Team();
        team.setId(10L);

        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .userId(2L)
                .roles(List.of(TeamRole.OWNER, TeamRole.DEVELOPER))
                .team(team)
                .name("JohnDoe")
                .description("A team developer")
                .accessLevel(5)
                .build();

        TeamMemberDto dto = mapper.toDto(teamMember);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getUserId());
        assertEquals(10L, dto.getTeam());
        assertEquals("JohnDoe", dto.getUsername());
        assertEquals("A team developer", dto.getDescription());
        assertEquals(5, dto.getAccessLevel());
        assertEquals(List.of("OWNER", "DEVELOPER"), dto.getRoles());
    }

    @Test
    void shouldMapDtoToTeamMemberCorrectly() {
        TeamMemberDto dto = TeamMemberDto.builder()
                .id(1L)
                .userId(2L)
                .team(10L)
                .roles(List.of("OWNER", "DEVELOPER"))
                .username("JohnDoe")
                .description("A team member")
                .accessLevel(5)
                .build();

        TeamMember teamMember = mapper.toEntity(dto);

        assertNotNull(teamMember);
        assertEquals(1L, teamMember.getId());
        assertEquals(2L, teamMember.getUserId());
        assertNotNull(teamMember.getTeam());
        assertEquals(10L, teamMember.getTeam().getId());
        assertEquals("JohnDoe", teamMember.getName());
        assertEquals("A team member", teamMember.getDescription());
        assertEquals(5, teamMember.getAccessLevel());
        assertEquals(List.of(TeamRole.OWNER, TeamRole.DEVELOPER), teamMember.getRoles());
    }

}
