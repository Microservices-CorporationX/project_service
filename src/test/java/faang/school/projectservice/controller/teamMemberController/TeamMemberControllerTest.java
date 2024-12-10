package faang.school.projectservice.controller.teamMemberController;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.controller.managingTeamMembers.ManagingTeamController;
import faang.school.projectservice.dto.managingTeamDto.TeamMemberFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.managingTeamService.ManagingTeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManagingTeamController.class)
@ContextConfiguration(classes = {ManagingTeamService.class, ManagingTeamController.class})
public class TeamMemberControllerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagingTeamService managingTeamService;

    @Test
    void testAddTeamMemberPositive() throws Exception {
        TeamMemberDto teamMemberDto = initializeTeamMemberController();

        when(managingTeamService.addTeamMember(1L, teamMemberDto, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(post("/managing-team/projects/1", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(teamMemberDto)));
    }

    @Test
    void testAddTeamMemberNegative() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();

        when(managingTeamService.addTeamMember(1L, teamMemberDto, 1L)).thenReturn(null);

        mockMvc.perform(post("/managing-team/projects/1", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddTeamMemberNegativeNotFound() throws Exception {
        TeamMemberDto teamMemberDto = initializeTeamMemberController();

        when(managingTeamService.addTeamMember(1L, teamMemberDto, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(post("/managing-team/projects", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPutTeamMemberPositive() throws Exception {
        TeamMemberDto teamMemberDto = initializeTeamMemberController();

        when(managingTeamService.updateTeamMember(1L, teamMemberDto, 1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/managing-team/projects/{projectId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1")
                        .param("currentUserId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(teamMemberDto)));
    }

    @Test
    void testPutTeamMemberNegative() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();

        when(managingTeamService.updateTeamMember(1L, teamMemberDto, 1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/managing-team/projects/{projectId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1")
                        .param("currentUserId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPutTeamMemberNegativeIsNotFound() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();

        when(managingTeamService.updateTeamMember(1L, teamMemberDto, 1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/managing-team/projects", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1")
                        .param("currentUserId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTeamMemberPositive() throws Exception {
        TeamMemberDto teamMemberDto = initializeTeamMemberController();

        when(managingTeamService.deleteTeamMember(1L, 1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(MockMvcRequestBuilders.delete("/managing-team/projects/{projectId}/{teamMemberId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currentUserId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(teamMemberDto)));
    }

    @Test
    void testDeleteTeamMemberNegative() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setId(2L);
        teamMemberDto.setTeam(2L);
        teamMemberDto.setUserId(2L);
        teamMemberDto.setUsername("testUser");
        teamMemberDto.setRoles(List.of("Developer"));
        teamMemberDto.setDescription("Team member description");
        teamMemberDto.setAccessLevel(1);

        when(managingTeamService.deleteTeamMember(1L, 1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(MockMvcRequestBuilders.delete("/managing-team/projects/{projectId}/{teamMemberId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteTeamMemberNegativeNotFound() throws Exception {
        TeamMemberDto teamMemberDto = initializeTeamMemberController();

        when(managingTeamService.deleteTeamMember(1L, 1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(MockMvcRequestBuilders.delete("/managing-team/projects/", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currentUserId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTeamMembersWithFilterPositive() throws Exception {
        TeamMemberDto teamMemberDto = initializeTeamMemberController();

        TeamMemberFilterDto teamMemberFilterDto = new TeamMemberFilterDto();
        teamMemberFilterDto.setRole(TeamRole.DEVELOPER);

        when(managingTeamService.getTeamMemberWithFilter(1L, teamMemberFilterDto))
                .thenReturn(List.of(teamMemberDto));

        mockMvc.perform(get("/managing-team/projects/{projectId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberFilterDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(List.of(teamMemberDto))));
    }

    @Test
    void testGetTeamMembersWithFilterNegative() throws Exception {
        TeamMemberFilterDto teamMemberFilterDto = new TeamMemberFilterDto();

        when(managingTeamService.getTeamMemberWithFilter(1L, teamMemberFilterDto))
                .thenReturn(List.of());

        mockMvc.perform(get("/managing-team/projects/{projectId}", 1L)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberFilterDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTeamMembersWithFilterPositiveNotFound() throws Exception {
        TeamMemberDto teamMemberDto = initializeTeamMemberController();

        TeamMemberFilterDto teamMemberFilterDto = new TeamMemberFilterDto();
        teamMemberFilterDto.setRole(TeamRole.DEVELOPER);

        when(managingTeamService.getTeamMemberWithFilter(1L, teamMemberFilterDto))
                .thenReturn(List.of(teamMemberDto));

        mockMvc.perform(get("/managing-team/projects/", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberFilterDto)))
                .andExpect(status().isNotFound());
    }


    @Test
    void testGetAllTeamMembersAllPositive() throws Exception {
        TeamMemberDto teamMemberDto = initializeTeamMemberController();

        when(managingTeamService.getAllMembers(1L)).thenReturn(List.of(teamMemberDto));

        mockMvc.perform(get("/managing-team/projects/{projectId}/all", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(List.of(teamMemberDto))));
    }

    @Test
    void testGetAllTeamMembersAllNegative() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();

        when(managingTeamService.getAllMembers(1L)).thenReturn(null);

        mockMvc.perform(get("/managing-team/projects/all", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllTeamMembersAllNegativeNotFound() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();

        when(managingTeamService.getAllMembers(1L)).thenReturn(null);

        mockMvc.perform(get("/managing-team/projects/", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTeamMemberPositive() throws Exception {
        TeamMemberDto teamMemberDto = initializeTeamMemberController();

        when(managingTeamService.getTeamMember(1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(get("/managing-team/projects/{projectId}/{teamMemberId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currentUserId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(teamMemberDto)));
    }

    @Test
    void testGetTeamMemberNegative() throws Exception {
        TeamMemberDto teamMemberDto = initializeTeamMemberController();


        when(managingTeamService.getTeamMember(1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(get("/managing-team/projects/{projectId}/{teamMemberId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currentUserId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(teamMemberDto)));
    }

    @Test
    void testGetTeamMemberNegativeNotFound() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();


        when(managingTeamService.getTeamMember(1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(get("/managing-team/projects/{projectId}/", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currentUserId", "1"))
                .andExpect(status().isNotFound());
    }

    private TeamMemberDto initializeTeamMemberController() {
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setId(1L);
        teamMemberDto.setTeam(1L);
        teamMemberDto.setUserId(1L);
        teamMemberDto.setUsername("testUser");
        teamMemberDto.setRoles(List.of("Developer"));
        teamMemberDto.setDescription("Team member description");
        teamMemberDto.setAccessLevel(1);
        return teamMemberDto;
    }
}
