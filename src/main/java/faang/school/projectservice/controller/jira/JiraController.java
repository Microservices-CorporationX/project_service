package faang.school.projectservice.controller.jira;

import faang.school.projectservice.dto.jira.issue.JiraIssueCreateDto;
import faang.school.projectservice.dto.jira.issue.JiraIssueDto;
import faang.school.projectservice.dto.jira.issue.filter.JiraIssueFilterDto;
import faang.school.projectservice.dto.jira.issue_link.JiraIssueLinkCreateDto;
import faang.school.projectservice.dto.jira.issue_transition.JiraIssueTransitionSetDto;
import faang.school.projectservice.dto.jira.issue.JiraIssueUpdateDto;
import faang.school.projectservice.dto.jira.issue.JiraIssueCreateResponseDto;
import faang.school.projectservice.dto.jira.issue_transition.JiraIssueAvailableTransitionsDto;
import faang.school.projectservice.service.jira.JiraService;
import feign.Response;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jira")
@RequiredArgsConstructor
@Validated
public class JiraController {

    private final JiraService jiraService;

    @PostMapping("/{jiraDomain}/issues")
    public ResponseEntity<JiraIssueCreateResponseDto> createIssue(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "Requester user ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") long requesterUserId,
            @PathVariable @NotBlank @Size(max = 64) String jiraDomain,
            @Valid @RequestBody JiraIssueCreateDto createDto
    ) {
        JiraIssueCreateResponseDto responseDto = jiraService.createIssue(jiraDomain, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/{jiraDomain}/issues/{issueKey}")
    public ResponseEntity<Void> updateIssue(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "Requester user ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") long requesterUserId,
            @PathVariable @NotBlank @Size(max = 64) String jiraDomain,
            @PathVariable @NotBlank String issueKey,
            @Valid @RequestBody JiraIssueUpdateDto updateDto
    ) {
        jiraService.updateIssue(jiraDomain, issueKey, updateDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{jiraDomain}/issues/{issueKey}/transitions")
    public ResponseEntity<JiraIssueAvailableTransitionsDto> getTransitionsForIssue(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "Requester user ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") long requesterUserId,
            @PathVariable @NotBlank @Size(max = 64) String jiraDomain,
            @PathVariable @NotBlank String issueKey
    ) {
        JiraIssueAvailableTransitionsDto responseDto = jiraService.getTransitionsForIssue(jiraDomain, issueKey);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{jiraDomain}/issues/{issueKey}/transitions")
    public ResponseEntity<Void> changeIssueStatus(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "Requester user ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") long requesterUserId,
            @PathVariable @NotBlank @Size(max = 64) String jiraDomain,
            @PathVariable @NotBlank String issueKey,
            @Valid @RequestBody JiraIssueTransitionSetDto transitionDto
    ) {
        jiraService.setStatusForIssue(jiraDomain, issueKey, transitionDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jiraDomain}/issueLink")
    public ResponseEntity<Void> createIssueLink(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "Requester user ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") long requesterUserId,
            @PathVariable @NotBlank @Size(max = 64) String jiraDomain,
            @Valid @RequestBody JiraIssueLinkCreateDto issueLinkCreateDto
    ) {
        jiraService.createIssueLink(jiraDomain, issueLinkCreateDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{jiraDomain}/issueLink/{issueLinkId}")
    public ResponseEntity<Void> deleteIssueLink(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "Requester user ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") long requesterUserId,
            @PathVariable @NotBlank @Size(max = 64) String jiraDomain,
            @PathVariable @NotBlank String issueLinkId
    ) {
        jiraService.deleteIssueLink(jiraDomain, issueLinkId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{jiraDomain}/issues/{issueKey}")
    public ResponseEntity<JiraIssueDto> getIssue(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "Requester user ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") long requesterUserId,
            @PathVariable @NotBlank @Size(max = 64) String jiraDomain,
            @PathVariable @NotBlank String issueKey
    ) {
        JiraIssueDto responseDto = jiraService.getIssue(jiraDomain, issueKey);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{jiraDomain}/issues")
    @PostMapping("/{jiraDomain}/issues/filter")
    public ResponseEntity<List<JiraIssueDto>> getIssues(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "Requester user ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") long requesterUserId,
            @PathVariable @NotBlank @Size(max = 64) String jiraDomain
    ) {
        List<JiraIssueDto> responseDtos = jiraService.getIssues(jiraDomain);
        return ResponseEntity.ok(responseDtos);
    }

    @PostMapping("/{jiraDomain}/issues/filter")
    public ResponseEntity<List<JiraIssueDto>> filterIssues(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "Requester user ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") long requesterUserId,
            @PathVariable @NotBlank @Size(max = 64) String jiraDomain,
            @RequestBody JiraIssueFilterDto filterDto
    ) {
        List<JiraIssueDto> responseDtos = jiraService.filterIssues(jiraDomain, filterDto);
        return ResponseEntity.ok(responseDtos);
    }
}
