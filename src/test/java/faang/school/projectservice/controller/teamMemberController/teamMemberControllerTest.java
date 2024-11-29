package faang.school.projectservice.controller.teamMemberController;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.controller.managingTeamMembers.ManagingTeamController;
import faang.school.projectservice.dto.managingTeamDto.TeamMemberFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.service.managingTeamService.managingTeamService;
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
@ContextConfiguration(classes = {managingTeamService.class, ManagingTeamController.class})
public class teamMemberControllerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private managingTeamService ManagingTeamService;

    @Test
    public void testAddTeamMemberPositive() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setId(1L);
        teamMemberDto.setTeam(1L);
        teamMemberDto.setUserId(1L);
        teamMemberDto.setUsername("testUser");
        teamMemberDto.setRoles(List.of("Developer"));
        teamMemberDto.setDescription("Team member description");
        teamMemberDto.setAccessLevel(1);

        when(ManagingTeamService.addTeamMember(1L, teamMemberDto, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(post("/managing-team/1", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(teamMemberDto)));
    }

    @Test
    public void testAddTeamMemberNegative() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();

        when(ManagingTeamService.addTeamMember(1L, teamMemberDto, 1L)).thenReturn(null);

        mockMvc.perform(post("/managing-team/1", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddTeamMemberNegativeNotFound() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setId(1L);
        teamMemberDto.setTeam(1L);
        teamMemberDto.setUserId(1L);
        teamMemberDto.setUsername("testUser");
        teamMemberDto.setRoles(List.of("Developer"));
        teamMemberDto.setDescription("Team member description");
        teamMemberDto.setAccessLevel(1);

        when(ManagingTeamService.addTeamMember(1L, teamMemberDto, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(post("/managing-team", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPutTeamMemberPositive() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setId(1L);
        teamMemberDto.setTeam(1L);
        teamMemberDto.setUserId(1L);
        teamMemberDto.setUsername("testUser");
        teamMemberDto.setRoles(List.of("Developer"));
        teamMemberDto.setDescription("Team member description");
        teamMemberDto.setAccessLevel(1);

        when(ManagingTeamService.updateTeamMember(1L, teamMemberDto, 1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/managing-team/{projectId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1")
                        .param("currentUserId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(teamMemberDto)));
    }

    @Test
    public void testPutTeamMemberNegative() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();

        when(ManagingTeamService.updateTeamMember(1L, teamMemberDto, 1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/managing-team/{projectId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1")
                        .param("currentUserId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPutTeamMemberNegativeIsNotFound() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();

        when(ManagingTeamService.updateTeamMember(1L, teamMemberDto, 1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/managing-team", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1")
                        .param("currentUserId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteTeamMemberPositive() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setId(1L);
        teamMemberDto.setTeam(1L);
        teamMemberDto.setUserId(1L);
        teamMemberDto.setUsername("testUser");
        teamMemberDto.setRoles(List.of("Developer"));
        teamMemberDto.setDescription("Team member description");
        teamMemberDto.setAccessLevel(1);

        when(ManagingTeamService.deleteTeamMember(1L, 1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(MockMvcRequestBuilders.delete("/managing-team/{projectId}/{teamMemberId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currentUserId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(teamMemberDto)));
    }

    @Test
    public void testDeleteTeamMemberNegative() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setId(2L);
        teamMemberDto.setTeam(2L);
        teamMemberDto.setUserId(2L);
        teamMemberDto.setUsername("testUser");
        teamMemberDto.setRoles(List.of("Developer"));
        teamMemberDto.setDescription("Team member description");
        teamMemberDto.setAccessLevel(1);

        when(ManagingTeamService.deleteTeamMember(1L, 1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(MockMvcRequestBuilders.delete("/managing-team/{projectId}/{teamMemberId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteTeamMemberNegativeNotFound() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setId(1L);
        teamMemberDto.setTeam(1L);
        teamMemberDto.setUserId(1L);
        teamMemberDto.setUsername("testUser");
        teamMemberDto.setRoles(List.of("Developer"));
        teamMemberDto.setDescription("Team member description");
        teamMemberDto.setAccessLevel(1);

        when(ManagingTeamService.deleteTeamMember(1L, 1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(MockMvcRequestBuilders.delete("/managing-team/", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currentUserId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetTeamMembersWithFilterPositive() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setId(1L);
        teamMemberDto.setTeam(1L);
        teamMemberDto.setUserId(1L);
        teamMemberDto.setUsername("testUser");
        teamMemberDto.setRoles(List.of("Developer"));
        teamMemberDto.setDescription("Team member description");
        teamMemberDto.setAccessLevel(1);

        TeamMemberFilterDto teamMemberFilterDto = new TeamMemberFilterDto();
        teamMemberFilterDto.setRole("Developer");

        when(ManagingTeamService.getTeamMemberWithFilter(1L, teamMemberFilterDto))
                .thenReturn(List.of(teamMemberDto));

        mockMvc.perform(get("/managing-team/{projectId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberFilterDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(List.of(teamMemberDto))));
    }

    @Test
    public void testGetTeamMembersWithFilterNegative() throws Exception {
        TeamMemberFilterDto teamMemberFilterDto = new TeamMemberFilterDto();

        when(ManagingTeamService.getTeamMemberWithFilter(1L, teamMemberFilterDto))
                .thenReturn(List.of());

        mockMvc.perform(get("/managing-team/{projectId}", 1L)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberFilterDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetTeamMembersWithFilterPositiveNotFound() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setId(1L);
        teamMemberDto.setTeam(1L);
        teamMemberDto.setUserId(1L);
        teamMemberDto.setUsername("testUser");
        teamMemberDto.setRoles(List.of("Developer"));
        teamMemberDto.setDescription("Team member description");
        teamMemberDto.setAccessLevel(1);

        TeamMemberFilterDto teamMemberFilterDto = new TeamMemberFilterDto();
        teamMemberFilterDto.setRole("Developer");

        when(ManagingTeamService.getTeamMemberWithFilter(1L, teamMemberFilterDto))
                .thenReturn(List.of(teamMemberDto));

        mockMvc.perform(get("/managing-team/", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberFilterDto)))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testGetAllTeamMembersAllPositive() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setId(1L);
        teamMemberDto.setTeam(1L);
        teamMemberDto.setUserId(1L);
        teamMemberDto.setUsername("testUser");
        teamMemberDto.setRoles(List.of("Developer"));
        teamMemberDto.setDescription("Team member description");
        teamMemberDto.setAccessLevel(1);

        when(ManagingTeamService.getAllMembers(1L)).thenReturn(List.of(teamMemberDto));

        mockMvc.perform(get("/managing-team/{projectId}/all", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(List.of(teamMemberDto))));
    }

    @Test
    public void testGetAllTeamMembersAllNegative() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();

        when(ManagingTeamService.getAllMembers(1L)).thenReturn(null);

        mockMvc.perform(get("/managing-team/all", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllTeamMembersAllNegativeNotFound() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();

        when(ManagingTeamService.getAllMembers(1L)).thenReturn(null);

        mockMvc.perform(get("/managing-team/", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberDto))
                        .param("teamMemberId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetTeamMemberPositive() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setId(1L);
        teamMemberDto.setTeam(1L);
        teamMemberDto.setUserId(1L);
        teamMemberDto.setUsername("testUser");
        teamMemberDto.setRoles(List.of("Developer"));
        teamMemberDto.setDescription("Team member description");
        teamMemberDto.setAccessLevel(1);

        when(ManagingTeamService.getTeamMember(1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(get("/managing-team/{projectId}/{teamMemberId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currentUserId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(teamMemberDto)));
    }

    @Test
    public void testGetTeamMemberNegative() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();


        when(ManagingTeamService.getTeamMember(1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(get("/managing-team/{projectId}/{teamMemberId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currentUserId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(teamMemberDto)));
    }

    @Test
    public void testGetTeamMemberNegativeNotFound() throws Exception {
        TeamMemberDto teamMemberDto = new TeamMemberDto();


        when(ManagingTeamService.getTeamMember(1L, 1L)).thenReturn(teamMemberDto);

        mockMvc.perform(get("/managing-team/{projectId}/", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currentUserId", "1"))
                .andExpect(status().isNotFound());
    }
}
