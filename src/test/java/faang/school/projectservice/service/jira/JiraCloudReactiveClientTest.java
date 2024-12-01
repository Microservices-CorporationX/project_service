package faang.school.projectservice.service.jira;

import faang.school.projectservice.config.jira.JiraUriConfig;
import faang.school.projectservice.dto.jira.issue.IssueResponse;
import faang.school.projectservice.dto.jira.issue.create.JiraCreateIssueRequest;
import faang.school.projectservice.dto.jira.issue.enums.IssueStatus;
import faang.school.projectservice.dto.jira.issue.search.JiraSearchRequest;
import faang.school.projectservice.dto.jira.issue.search.JiraSearchResponse;
import faang.school.projectservice.dto.jira.issue.transition.TransitionNestedResponse;
import faang.school.projectservice.dto.jira.issue.transition.TransitionRequest;
import faang.school.projectservice.dto.jira.issue.transition.TransitionsResponse;
import faang.school.projectservice.dto.jira.issue.update.JiraUpdateIssueRequest;
import faang.school.projectservice.exceptions.jira.JiraBadRequestException;
import faang.school.projectservice.exceptions.jira.JiraNotFoundException;
import faang.school.projectservice.service.jira.builders.JiraCreateIssueBuilder;
import faang.school.projectservice.service.jira.builders.JiraSearchRequestBuilder;
import faang.school.projectservice.service.jira.builders.JiraUpdateIssueBuilder;
import faang.school.projectservice.service.jira.search.conditions.impl.ProjectCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JiraCloudReactiveClientTest {

    private static final String JIRA_NOT_FOUND_MESSAGE = "Jira not found error: %s";
    private static final String JIRA_BAD_REQUEST_MESSAGE = "Jira bad request error: %s";

    @Mock
    private WebClient webClient;

    @Mock
    private JiraUriConfig uriConfig;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private JiraCloudReactiveClient jiraClient;

    @Test
    void testCreateIssue_Success() {
        JiraCreateIssueRequest request = new JiraCreateIssueBuilder()
                .setProjectKey("TEST")
                .setSummary("Test Issue")
                .setDescription("Test Description")
                .build();

        IssueResponse expectedResponse = new IssueResponse();
        expectedResponse.setKey("TEST-1");

        when(uriConfig.getCreateIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue");
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uriConfig.getCreateIssueUri())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueResponse.class)).thenReturn(Mono.just(expectedResponse));

        Mono<IssueResponse> actualResponse = jiraClient.createIssue(request);

        StepVerifier.create(actualResponse)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void testCreateIssue_BadRequest() {
        JiraCreateIssueRequest request = new JiraCreateIssueBuilder()
                .setProjectKey("TEST")
                .setSummary("Test Issue")
                .setDescription("Test Description")
                .build();
        String bodyAsString = "bodyAsString";
        String expectedErrorMessage = String.format(JIRA_BAD_REQUEST_MESSAGE, bodyAsString);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uriConfig.getCreateIssueUri())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueResponse.class))
                .thenReturn(Mono.error(new JiraBadRequestException(bodyAsString)));

        Mono<IssueResponse> actualResponse = jiraClient.createIssue(request);

        StepVerifier.create(actualResponse)
                .expectErrorMatches(throwable -> throwable instanceof JiraBadRequestException &&
                        throwable.getMessage().equals(expectedErrorMessage))
                .verify();
    }

    @Test
    void testCreateIssue_NotFound() {
        JiraCreateIssueRequest request = new JiraCreateIssueBuilder()
                .setProjectKey("TEST")
                .setSummary("Test Issue")
                .setDescription("Test Description")
                .build();
        String bodyAsString = "bodyAsString";
        String expectedErrorMessage = String.format(JIRA_NOT_FOUND_MESSAGE, bodyAsString);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uriConfig.getCreateIssueUri())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueResponse.class))
                .thenReturn(Mono.error(new JiraNotFoundException(bodyAsString)));

        Mono<IssueResponse> actualResponse = jiraClient.createIssue(request);

        StepVerifier.create(actualResponse)
                .expectErrorMatches(throwable -> throwable instanceof JiraNotFoundException &&
                        throwable.getMessage().equals(expectedErrorMessage))
                .verify();
    }

    @Test
    void testUpdateIssue_success() {
        JiraUpdateIssueRequest request = new JiraUpdateIssueBuilder()
                .setSummary("test")
                .setDescription("test description")
                .build();
        String issueId = "TEST-1";
        IssueResponse expectedResponse = new IssueResponse();
        expectedResponse.setKey("TEST-1");

        when(uriConfig.getUpdateIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue/%s");
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(String.format(uriConfig.getUpdateIssueUri(), issueId))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueResponse.class)).thenReturn(Mono.just(expectedResponse));

        Mono<IssueResponse> actualResponse = jiraClient.updateIssue(issueId, request);

        StepVerifier.create(actualResponse)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void testUpdateIssue_badRequest() {
        JiraUpdateIssueRequest request = new JiraUpdateIssueBuilder()
                .setSummary("test")
                .setDescription("test description")
                .build();
        String issueId = "TEST-1";
        IssueResponse expectedResponse = new IssueResponse();
        expectedResponse.setKey("TEST-1");
        String bodyAsString = "bodyAsString";
        String expectedErrorMessage = String.format(JIRA_BAD_REQUEST_MESSAGE, bodyAsString);

        when(uriConfig.getUpdateIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue/%s");
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(String.format(uriConfig.getUpdateIssueUri(), issueId))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueResponse.class))
                .thenReturn(Mono.error(new JiraBadRequestException(bodyAsString)));

        Mono<IssueResponse> actualResponse = jiraClient.updateIssue(issueId, request);

        StepVerifier.create(actualResponse)
                .expectErrorMatches(throwable -> throwable instanceof JiraBadRequestException &&
                        throwable.getMessage().equals(expectedErrorMessage))
                .verify();
    }

    @Test
    void testUpdateIssue_NotFound() {
        JiraUpdateIssueRequest request = new JiraUpdateIssueBuilder()
                .setSummary("test")
                .setDescription("test description")
                .build();
        String issueId = "TEST-1";
        IssueResponse expectedResponse = new IssueResponse();
        expectedResponse.setKey("TEST-1");
        String bodyAsString = "bodyAsString";
        String expectedErrorMessage = String.format(JIRA_NOT_FOUND_MESSAGE, bodyAsString);

        when(uriConfig.getUpdateIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue/%s");
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(String.format(uriConfig.getUpdateIssueUri(), issueId))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueResponse.class))
                .thenReturn(Mono.error(new JiraNotFoundException(bodyAsString)));

        Mono<IssueResponse> actualResponse = jiraClient.updateIssue(issueId, request);

        StepVerifier.create(actualResponse)
                .expectErrorMatches(throwable -> throwable instanceof JiraNotFoundException &&
                        throwable.getMessage().equals(expectedErrorMessage))
                .verify();
    }

    @Test
    void testTransitionIssue_success() {
        IssueStatus newIssueStatus = IssueStatus.DONE;
        TransitionRequest request = TransitionRequest.builder()
                .transition(new TransitionRequest.TransitionNested(newIssueStatus.getId()))
                .build();
        String issueKey = "TEST-1";

        when(uriConfig.getTransitionIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue/{issueKey}/transitions");
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(String.format(uriConfig.getTransitionIssueUri(), issueKey)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        Mono<Void> monoResponse = jiraClient.transitionIssue(issueKey, newIssueStatus);

        StepVerifier.create(monoResponse)
                .verifyComplete();
    }

    @Test
    void testTransitionIssue_badRequest() {
        IssueStatus newIssueStatus = IssueStatus.DONE;
        TransitionRequest request = TransitionRequest.builder()
                .transition(new TransitionRequest.TransitionNested(newIssueStatus.getId()))
                .build();
        String issueKey = "TEST-1";
        String bodyAsString = "bodyAsString";
        String expectedErrorMessage = String.format(JIRA_BAD_REQUEST_MESSAGE, bodyAsString);

        when(uriConfig.getTransitionIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue/{issueKey}/transitions");
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(String.format(uriConfig.getTransitionIssueUri(), issueKey)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.error(new JiraBadRequestException(bodyAsString)));

        Mono<Void> monoResponse = jiraClient.transitionIssue(issueKey, newIssueStatus);

        StepVerifier.create(monoResponse)
                .verifyErrorMatches(throwable -> throwable instanceof JiraBadRequestException &&
                        throwable.getMessage().equals(expectedErrorMessage));
    }

    @Test
    void testTransitionIssue_notFound() {
        IssueStatus newIssueStatus = IssueStatus.DONE;
        TransitionRequest request = TransitionRequest.builder()
                .transition(new TransitionRequest.TransitionNested(newIssueStatus.getId()))
                .build();
        String issueKey = "TEST-1";
        String bodyAsString = "bodyAsString";
        String expectedErrorMessage = String.format(JIRA_NOT_FOUND_MESSAGE, bodyAsString);

        when(uriConfig.getTransitionIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue/{issueKey}/transitions");
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(String.format(uriConfig.getTransitionIssueUri(), issueKey)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.error(new JiraNotFoundException(bodyAsString)));

        Mono<Void> monoResponse = jiraClient.transitionIssue(issueKey, newIssueStatus);

        StepVerifier.create(monoResponse)
                .verifyErrorMatches(throwable -> throwable instanceof JiraNotFoundException &&
                        throwable.getMessage().equals(expectedErrorMessage));
    }

    @Test
    void testGetAvailableTransitions_success() {
        String issueKey = "TEST-1";

        TransitionNestedResponse responseNested1 = TransitionNestedResponse.builder()
                .id("1")
                .name("test1")
                .build();
        TransitionNestedResponse responseNested2 = TransitionNestedResponse.builder()
                .id("1")
                .name("test1")
                .build();
        List<TransitionNestedResponse> expectedNestedTransitions = List.of(responseNested1, responseNested2);
        TransitionsResponse expectedTransitionResponse =
                new TransitionsResponse("test", expectedNestedTransitions);

        when(uriConfig.getTransitionIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue/{issueKey}/transitions");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(String.format(uriConfig.getTransitionIssueUri(), issueKey)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(TransitionsResponse.class)).thenReturn(Mono.just(expectedTransitionResponse));

        Mono<List<TransitionNestedResponse>> actualTransitions = jiraClient.getAvailableTransitions(issueKey);

        StepVerifier.create(actualTransitions)
                .expectNext(expectedNestedTransitions)
                .verifyComplete();
    }

    @Test
    void testSearchIssuesByFilter() {
        JiraSearchRequest request = new JiraSearchRequestBuilder()
                .withDefaultFields()
                .addCondition(new ProjectCondition("TP"))
                .build();

        IssueResponse expectedResponse1 = new IssueResponse();
        expectedResponse1.setKey("TP-1");
        IssueResponse expectedResponse2 = new IssueResponse();
        expectedResponse2.setKey("TP-1");

        List<IssueResponse> expectedIssues = List.of(expectedResponse1, expectedResponse2);
        JiraSearchResponse expectedResponse = new JiraSearchResponse();
        expectedResponse.setIssues(expectedIssues);

        when(uriConfig.getSearchUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/search");
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uriConfig.getSearchUri())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JiraSearchResponse.class)).thenReturn(Mono.just(expectedResponse));

        Mono<List<IssueResponse>> actualIssuesResponses = jiraClient.searchIssuesByFilter(request);

        StepVerifier.create(actualIssuesResponses)
                .expectNext(expectedIssues)
                .verifyComplete();
    }
}

