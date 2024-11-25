package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamMemberServiceTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TeamMemberService teamMemberService;

    private List<TeamMember> teamMembers;


    @BeforeEach
    public void setUp() {
        teamMembers = new ArrayList<>();

        TeamMember teamMember = new TeamMember();
        teamMember.setId(1L);
        teamMember.setRoles(List.of(TeamRole.INTERN));
        teamMembers.add(teamMember);

        TeamMember teamMember2 = new TeamMember();
        teamMember2.setId(2L);
        teamMembers.add(teamMember2);
    }

    @Test
    public void testGetTeamMemberById() {
        when(teamMemberRepository.findById(1L)).thenReturn(teamMembers.get(0));

        assertEquals(teamMembers.get(0), teamMemberService.getTeamMemberById(1L));
    }

    @Test
    public void testGetTeamMemberByIdNotFound() {
        when(teamMemberRepository.findById(1L)).thenReturn(null);

        assertNull(teamMemberService.getTeamMemberById(1L));
    }

    @Test
    public void testGetAllTeamMembersByIds() {
        when(teamMemberRepository.findAllByIds(List.of(1L, 2L))).thenReturn(teamMembers);

        assertEquals(teamMembers, teamMemberService.getAllTeamMembersByIds(List.of(1L, 2L)));
    }

    @Test
    void testSetTeamMembersRoleAndRemoveInternRole() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(teamMemberRepository.findAllByIds(ids)).thenReturn(teamMembers);

        teamMemberService.setTeamMembersRoleAndRemoveInternRole(ids, TeamRole.MANAGER);

        verify(teamMemberRepository, times(1)).findAllByIds(ids);
        verify(teamMemberRepository, times(1)).saveAll(teamMembers);

        TeamMember updatedMember1 = teamMembers.get(0);
        assertTrue(updatedMember1.getRoles().contains(TeamRole.MANAGER));
        assertFalse(updatedMember1.getRoles().contains(TeamRole.INTERN));
    }


    @Test
    public void testRemoveTeamMemberRole(){
        TeamMember member = teamMembers.get(0);
        TeamRole role = TeamRole.INTERN;

        teamMemberService.removeTeamRole(member, role);

        assertFalse(member.getRoles().contains(role));
        verify(teamMemberRepository, times(1)).save(member);
    }
}
