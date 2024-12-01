package faang.school.projectservice.controller;

import faang.school.projectservice.dto.jira.issue.IssueResponse;
import faang.school.projectservice.dto.jira.issue.create.JiraCreateIssueRequest;
import faang.school.projectservice.dto.jira.issue.enums.IssueStatus;
import faang.school.projectservice.dto.jira.issue.enums.IssueType;
import faang.school.projectservice.dto.jira.issue.update.JiraUpdateIssueRequest;
import faang.school.projectservice.dto.jira.issue.search.JiraSearchRequest;
import faang.school.projectservice.dto.jira.issue.transition.TransitionNestedResponse;
import faang.school.projectservice.service.jira.JiraCloudReactiveClient;
import faang.school.projectservice.service.jira.builders.JiraCreateIssueBuilder;
import faang.school.projectservice.service.jira.builders.JiraSearchRequestBuilder;
import faang.school.projectservice.service.jira.builders.JiraUpdateIssueBuilder;
import faang.school.projectservice.service.jira.search.conditions.impl.AssigneeCondition;
import faang.school.projectservice.service.jira.search.conditions.impl.ProjectCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskV1Controller {

    private final JiraCloudReactiveClient jiraCloudReactiveClient;

    @PostMapping
    public Mono<IssueResponse> createTask() {
        JiraCreateIssueRequest request = new JiraCreateIssueBuilder()
                .setProjectKey("TP")
                .setSummary("summary")
                .setIssueType(IssueType.TASK)
                .setDescription("descriptionTest")
                .build();
        return jiraCloudReactiveClient.createIssue(request);
    }

    @PutMapping
    public Mono<IssueResponse> updateTask() {

        JiraUpdateIssueRequest request = new JiraUpdateIssueBuilder()
                .setSummary("updated new new new")
                .setDescription("something1")
                .setAssigneeAccountId("712020:8e054812-7423-4df6-9787-6216bc3ab575")
                .setLinkedIssueKeys(List.of("TP-24"))
                .setIssueType(IssueType.BUG)
                .build();

        return jiraCloudReactiveClient.updateIssue("TP-36", request);
    }

    @PutMapping("/transition/{issueId}")
    public Mono<Void> transitionIssue(@PathVariable String issueId, @RequestParam String transitionId) {
        return jiraCloudReactiveClient.transitionIssue(issueId, IssueStatus.DONE);
    }

    @GetMapping("/transitions")
    public Mono<List<TransitionNestedResponse>> getAvailableTransitions() {
        return jiraCloudReactiveClient.getAvailableTransitions("TP-30");
    }

    @GetMapping("/search")
    public Mono<List<IssueResponse>> searchTasksByFilter() {
        JiraSearchRequest request = new JiraSearchRequestBuilder()
                .withDefaultFields()
                .addCondition(new ProjectCondition("TP"))
                .addCondition(new AssigneeCondition("Николай Орленко"))
                .build();
        return jiraCloudReactiveClient.searchIssuesByFilter(request);
    }

    @GetMapping("/search/{key}")
    public Mono<IssueResponse> searchTaskByKey(@PathVariable String key) {
        return jiraCloudReactiveClient.getIssueByKey(key);
    }

    @GetMapping("/jira/{projectKey}")
    public Mono<List<IssueResponse>> getIssuesByProjKey(@PathVariable String projectKey) {
        return jiraCloudReactiveClient.getIssuesByProject(projectKey);
    }
}
