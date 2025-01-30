package faang.school.projectservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "jiraClient", url = "${jira.url}", configuration = JiraFeignConfig.class)
public interface JiraFeignClient {

    @PostMapping("/issue")
    ResponseEntity<JiraIssueResponse> createIssue(@RequestBody JiraIssueRequest request);

    @PutMapping("/issue/{issueIdOrKey}")
    ResponseEntity<Void> updateIssue(
            @PathVariable("issueIdOrKey") String issueIdOrKey,
            @RequestBody JiraIssueRequest request
    );

    @GetMapping("/issue/{issueIdOrKey}")
    ResponseEntity<JiraIssueResponse> getIssue(
            @PathVariable("issueIdOrKey") String issueIdOrKey,
            @RequestParam(value = "fields", required = false) String fields
    );

    @GetMapping("/search")
    ResponseEntity<JiraSearchResponse> searchIssues(
            @RequestParam("jql") String jql,
            @RequestParam(value = "startAt", required = false) Integer startAt,
            @RequestParam(value = "maxResults", required = false) Integer maxResults,
            @RequestParam(value = "fields", required = false) String fields
    );

    @PostMapping("/issue/{issueIdOrKey}/transitions")
    ResponseEntity<Void> transitionIssue(
            @PathVariable("issueIdOrKey") String issueIdOrKey,
            @RequestBody JiraTransitionRequest request
    );

    @PostMapping("/issue/{issueIdOrKey}/remotelinks")
    ResponseEntity<Void> createIssueLink(
            @PathVariable("issueIdOrKey") String issueIdOrKey,
            @RequestBody JiraIssueLinkRequest request
    );
}