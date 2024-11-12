package faang.school.projectservice.controller.jira;

import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.service.jira.JiraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("api/v1/jira")
public class JiraController {

    private final JiraService jiraService;

    @PostMapping("/{projectKey}")
    public IssueDto createIssue(@PathVariable String projectKey,
                                @RequestBody IssueDto issueDto) {
        return jiraService.createIssue(projectKey, issueDto);
    }

    @GetMapping("/{projectKey}/filters")
    public List<IssueDto> getAllIssueByFilter(@PathVariable String projectKey,
                                              @Valid @RequestBody IssueFilterDto filter) {
        return jiraService.getAllIssueByFilter(projectKey, filter);
    }

    @GetMapping("/all/{projectKey}")
    public List<IssueDto> getAllIssues(@PathVariable String projectKey) {
        return jiraService.getAllIssues(projectKey);
    }

    @GetMapping("/{issueKey}")
    public IssueDto getIssue(@PathVariable String issueKey) {
        return jiraService.getIssue(issueKey);
    }
}