package faang.school.projectservice.controller.jira;

import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.JiraAccountDto;
import faang.school.projectservice.service.jira.JiraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/jira")
public class JiraController {

    private final JiraService jiraService;

    @GetMapping("/issue/{issueKey}")
    public IssueDto getIssue(@RequestBody JiraAccountDto jiraAccountDto, @PathVariable String issueKey) {
        return jiraService.getIssueBiKey(jiraAccountDto, issueKey);
    }

    @GetMapping("/project/{projectKey}/issues")
    public List<IssueDto> getAllIssue(@RequestBody JiraAccountDto jiraAccountDto, @PathVariable String projectKey) {
        return jiraService.getAllIssues(jiraAccountDto, projectKey);
    }

    @GetMapping("/project/{projectKey}/issues/{statusId}")
    public List<IssueDto> getIssueWithFilterByStatus(@RequestBody JiraAccountDto jiraAccountDto, @PathVariable String projectKey,
                                                     @PathVariable Long statusId) {
        return jiraService.getIssueWithFilterByStatus(jiraAccountDto, projectKey, statusId);
    }

    @GetMapping("/project/{projectKey}/issues/assignee/")
    public List<IssueDto> getIssuesByExecutorId(@RequestBody JiraAccountDto jiraAccountDto,
                                                @PathVariable String projectKey, @RequestParam String assigneeId) {
        return jiraService.getIssueByExecutorId(jiraAccountDto, projectKey, assigneeId);
    }

    @PostMapping("/issue")
    public String createIssue(@Valid @RequestBody JiraAccountDto jiraAccountDto, @RequestParam IssueDto issueDto) {
        return jiraService.createIssue(jiraAccountDto, issueDto);
    }
}
