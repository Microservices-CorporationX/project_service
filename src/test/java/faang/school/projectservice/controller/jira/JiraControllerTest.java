package faang.school.projectservice.controller.jira;

import faang.school.projectservice.config.context.UserHeaderFilter;
import faang.school.projectservice.dto.jira.request.create.IssueCreateRequestDto;
import faang.school.projectservice.dto.jira.request.create.IssueFieldsCreateRequestDto;
import faang.school.projectservice.dto.jira.request.update.IssueFieldsUpdateRequestDto;
import faang.school.projectservice.dto.jira.request.update.IssueUpdateRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueDto;
import faang.school.projectservice.dto.jira.response.IssueFieldsResponseDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.service.jira.JiraService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(JiraController.class)
class JiraControllerTest {


    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JiraService jiraService;

    @MockBean
    private UserHeaderFilter filter;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private final String basePath = "/api/jira/issues";

    @Test
    public void testGetAllIssues() throws Exception {
        String projectId = "PROJ";
        IssueResponseDto responseDto = new IssueResponseDto();
        responseDto.setIssues(Collections.singletonList(new IssueDto("1",
                IssueFieldsResponseDto.builder().summary("Test Issue").build())));

        when(jiraService.getAllIssues(projectId)).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/project/{projectId}", projectId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetIssuesById() throws Exception {
        String issueId = "1";
        IssueDto issueDto = new IssueDto("1",
                IssueFieldsResponseDto.builder().summary("Test Issue").build());

        when(jiraService.getIssueById(issueId)).thenReturn(issueDto);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/{id}", issueId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetIssuesByAssignee() throws Exception {
        String userId = "user123";
        IssueResponseDto responseDto = new IssueResponseDto();
        responseDto.setIssues(Collections.singletonList(new IssueDto("1",
                IssueFieldsResponseDto.builder().summary("Test Issue").build())));

        when(jiraService.getIssuesByAssignee(userId)).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/assignee/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetIssuesByStatus() throws Exception {
        String status = "In Progress";
        IssueResponseDto responseDto = new IssueResponseDto();
        responseDto.setIssues(Collections.singletonList(new IssueDto("1",
                IssueFieldsResponseDto.builder().summary("Test Issue").build())));

        when(jiraService.getIssuesByStatus(status)).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/status/{status}", status)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateIssue() throws Exception {
        IssueCreateResponseDto responseDto = new IssueCreateResponseDto();
        responseDto.setId("1");
        responseDto.setKey("PROJ-1");

        Mockito.when(jiraService.createIssue(getIssueCreateRequestDto())).thenReturn(responseDto);

        String requestBody = "{fields: {\"summary\":\"Test Issue\",\"description\":\"Test Description\"}}";

        mockMvc.perform(MockMvcRequestBuilders.post(basePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testEditIssue() throws Exception {
        String issueId = "1";
        IssueUpdateRequestDto requestDto = getIssueUpdateRequestDto();

        String requestBody = "{\"summary\":\"Updated Summary\",\"description\":\"Updated Description\"}";

        mockMvc.perform(MockMvcRequestBuilders.put(basePath + "/{issueId}", issueId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());

        Mockito.verify(jiraService, Mockito.times(1)).editIssue(issueId, requestDto);
    }

    private IssueCreateRequestDto getIssueCreateRequestDto() {
        IssueCreateRequestDto requestDto = new IssueCreateRequestDto();
        requestDto.setFields(
                IssueFieldsCreateRequestDto.builder().summary("Test Issue").description("Test Description").build());
        return requestDto;
    }

    private IssueUpdateRequestDto getIssueUpdateRequestDto() {
        IssueUpdateRequestDto requestDto = new IssueUpdateRequestDto();
        requestDto.setFields(
                IssueFieldsUpdateRequestDto.builder().summary("Updated Summary").description("Updated Description").build());
        return requestDto;
    }

}