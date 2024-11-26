package faang.school.projectservice.controller.members;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.dto.team.TeamMemberCreateDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.dto.team.TeamMemberUpdateDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {TeamMembersController.class})
class TeamMembersControllerTest {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final long TEAM_MEMBER_ID = 1L;
    public static final long TEAM_ID = 1L;
    @MockBean
    TeamMemberService teamMemberService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void addTeamMember() throws Exception {
        TeamMemberCreateDto teamMemberCreateDto = getTeamMemberCreateDto();
        TeamMemberDto teamMemberDto = getTeamMemberDto();

        when(teamMemberService.addTeamMember(TEAM_ID, teamMemberCreateDto)).thenReturn(teamMemberDto);

        mockMvc.perform(post("/api/v1/team_members/{teamId}", TEAM_ID).contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(teamMemberCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(teamMemberDto)));
        verify(teamMemberService, times(1)).addTeamMember(TEAM_ID, teamMemberCreateDto);
    }

    @Test
    void updateTeamMember() throws Exception {
        TeamMemberUpdateDto teamMemberUpdateDto = getTeamMemberUpdateDto();
        TeamMemberDto teamMemberDto = getTeamMemberDto();

        when(teamMemberService.updateTeamMember(TEAM_ID, TEAM_MEMBER_ID, teamMemberUpdateDto)).thenReturn(teamMemberDto);

        mockMvc.perform(put("/api/v1/team_members/{teamId}/{teamMemberId}", TEAM_ID, TEAM_MEMBER_ID)
                        .contentType(MediaType.APPLICATION_JSON).content(OBJECT_MAPPER.writeValueAsString(teamMemberUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(teamMemberDto)));

        verify(teamMemberService, times(1)).updateTeamMember(TEAM_ID, TEAM_MEMBER_ID, teamMemberUpdateDto);
    }


    @Test
    void deleteTeamMember() throws Exception {
        doNothing().when(teamMemberService).deleteTeamMember(TEAM_MEMBER_ID, TEAM_ID);

        mockMvc.perform(delete("/api/v1/team_members/{teamMemberId}/{teamId}", TEAM_MEMBER_ID, TEAM_ID))
                .andExpect(status().isNoContent());

        verify(teamMemberService, times(1)).deleteTeamMember(TEAM_MEMBER_ID, TEAM_ID);
    }

    @Test
    void getTeamMembersByFilter() throws Exception {
        TeamMemberDto teamMemberDto2 = TeamMemberDto.builder()
                .teamMemberId(2L)
                .roles(List.of(TeamRole.MANAGER))
                .build();
        TeamFilterDto filterDto = new TeamFilterDto();
        filterDto.setTeamRole(TeamRole.MANAGER);
        List<TeamMemberDto> filteredTeamMembers = List.of(teamMemberDto2);

        when(teamMemberService.getTeamMembersByFilter(eq(TEAM_ID), eq(filterDto))).thenReturn(filteredTeamMembers);

        mockMvc.perform(get("/api/v1/team_members/{teamId}/filter", TEAM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(filterDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].teamMemberId", is(2)))
                .andExpect(jsonPath("$[0].roles[0]", is("MANAGER")));


        verify(teamMemberService, times(1)).getTeamMembersByFilter(eq(TEAM_ID), eq(filterDto));
    }

    @Test
    void getAllTeamMembers() throws Exception {
        TeamMemberDto teamMemberDto1 = getTeamMemberDto();
        TeamMemberDto teamMemberDto2 = TeamMemberDto.builder()
                .teamMemberId(2L)
                .roles(List.of(TeamRole.MANAGER))
                .build();
        List<TeamMemberDto> teamMembers = List.of(teamMemberDto1, teamMemberDto2);
        Page<TeamMemberDto> page = new PageImpl<>(teamMembers, PageRequest.of(0, 10), teamMembers.size());

        when(teamMemberService.getAllTeamMembers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/team_members/all")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].teamMemberId", is(1)))  // Проверяем первый элемент
                .andExpect(jsonPath("$.content[1].teamMemberId", is(2)))  // Проверяем второй элемент
                .andExpect(jsonPath("$.totalElements", is(2)))  // Проверяем общее количество элементов
                .andExpect(jsonPath("$.totalPages", is(1)))  // Проверяем количество страниц
                .andExpect(jsonPath("$.size", is(10)));

        verify(teamMemberService, times(1)).getAllTeamMembers(any(Pageable.class));
    }

    @Test
    void getTeamMemberById() throws Exception {
        TeamMemberDto teamMemberDto = getTeamMemberDto();

        when(teamMemberService.getTeamMemberById(TEAM_MEMBER_ID)).thenReturn(teamMemberDto);

        mockMvc.perform(get("/api/v1/team_members/{teamMemberId}", TEAM_MEMBER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(teamMemberDto)));

        verify(teamMemberService, times(1)).getTeamMemberById(TEAM_MEMBER_ID);
    }

    private static TeamMemberCreateDto getTeamMemberCreateDto() {
        return TeamMemberCreateDto
                .builder()
                .userId(1L)
                .roles(List.of(TeamRole.TEAMLEAD))
                .build();
    }

    private static TeamMemberUpdateDto getTeamMemberUpdateDto() {
        return TeamMemberUpdateDto
                .builder()
                .roles(List.of(TeamRole.MANAGER))
                .build();
    }

    private static TeamMemberDto getTeamMemberDto() {
        return TeamMemberDto.builder()
                .teamMemberId(1L)
                .roles(List.of(TeamRole.TEAMLEAD))
                .build();
    }
}