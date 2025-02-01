package faang.school.projectservice.controller.jira;

import faang.school.projectservice.dto.jira.request.create.IssueCreateRequestDto;
import faang.school.projectservice.dto.jira.request.update.IssueUpdateRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.service.jira.JiraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/jira/issues")
public class JiraController {

    private final JiraService jiraService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<IssueResponseDto> getAllIssues(@PathVariable("projectId") String projectId) {
        return new ResponseEntity<>(jiraService.getAllIssues(projectId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueDto> getIssuesById(@PathVariable("id") String id) {
        return new ResponseEntity<>(jiraService.getIssueById(id), HttpStatus.OK);
    }

    @GetMapping("/assignee/{userId}")
    public ResponseEntity<IssueResponseDto> getIssuesByAssignee(@PathVariable("userId") String userId) {
        return new ResponseEntity<>(jiraService.getIssuesByAssignee(userId), HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<IssueResponseDto> getIssuesByStatus(@PathVariable("status") String status) {
        return new ResponseEntity<>(jiraService.getIssuesByStatus(status), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<IssueCreateResponseDto> createIssue(@RequestBody IssueCreateRequestDto dto) {
        return new ResponseEntity<>(jiraService.createIssue(dto), HttpStatus.CREATED);
    }


    @PutMapping("/{issueId}")
    public ResponseEntity<Void> editIssue(@PathVariable("issueId") String issueId, @RequestBody IssueUpdateRequestDto dto) {
        jiraService.editIssue(issueId, dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
