package faang.school.projectservice.service.jira;

import faang.school.projectservice.dto.jira.request.update.IssueUpdateRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueDto;
import faang.school.projectservice.dto.jira.request.create.IssueCreateRequestDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;

public interface JiraService {

    IssueResponseDto getAllIssues(String projectId);

    IssueDto getIssueById(String issueId);

    IssueResponseDto getIssuesByAssignee(String assigneeId);

    IssueResponseDto getIssuesByStatus(String issueStatus);

    IssueCreateResponseDto createIssue(IssueCreateRequestDto requestDto);

    void editIssue(String issueId, IssueUpdateRequestDto requestDto);
}
