package faang.school.projectservice.service.jira;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.webclient.jira.JiraClientConfig;
import faang.school.projectservice.dto.jira.issue.JiraIssueBaseDto;
import faang.school.projectservice.dto.jira.issue.JiraIssueCreateDto;
import faang.school.projectservice.dto.jira.issue.JiraIssueCreateResponseDto;
import faang.school.projectservice.dto.jira.issue.JiraIssueDto;
import faang.school.projectservice.dto.jira.issue.JiraFilteredIssuesDto;
import faang.school.projectservice.dto.jira.issue.filter.JiraIssueFilterDto;
import faang.school.projectservice.dto.jira.issue_link.JiraIssueLinkCreateDto;
import faang.school.projectservice.dto.jira.issue_transition.JiraIssueTransitionSetDto;
import faang.school.projectservice.dto.jira.issue.JiraIssueUpdateDto;
import faang.school.projectservice.dto.jira.issue_transition.JiraIssueAvailableTransitionsDto;
import faang.school.projectservice.dto.user_jira.UserJiraDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.webclient.WebClientErrorHandler;
import faang.school.projectservice.filter.jira.issue.JiraIssueFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraService {

    private final UserContext userContext;
    private final JiraClientConfig jiraConfig;
    private final UserServiceClient userServiceClient;
    private final WebClientErrorHandler webClientErrorHandler;
    private final List<JiraIssueFilter> issueFilters;

    @Value("${services.jira.endpoints.issue}")
    private String issueEndpoint;

    @Value("${services.jira.endpoints.issue-link}")
    private String issueLinkEndpoint;

    @Value("${services.jira.endpoints.search}")
    private String searchEndpoint;

    public JiraIssueCreateResponseDto createIssue(String jiraDomain, JiraIssueCreateDto createDto) {
        long userId = userContext.getUserId();
        log.info("Received request from user with ID {} to create a Jira issue in domain {}", userId, jiraDomain);
        validateSubtaskType(createDto);

        JiraIssueCreateResponseDto responseDto =
                executeJiraRequest(jiraDomain, HttpMethod.POST, issueEndpoint, createDto, JiraIssueCreateResponseDto.class);

        log.info("Jira issue was created in domain {} by user with ID {}", jiraDomain, userId);
        return responseDto;
    }

    public void updateIssue(String jiraDomain, String issueKey, JiraIssueUpdateDto updateDto) {
        long userId = userContext.getUserId();
        log.info("Received request from user with ID {} to update Jira issue with key={} in domain {}", userId, issueKey, jiraDomain);
        validateSubtaskType(updateDto);

        String issueUri = getIssueUri(issueKey);
        executeJiraRequest(jiraDomain, HttpMethod.PUT, issueUri, updateDto, Void.class);

        log.info("Jira issue with key={} in domain {} was updated by user with ID {}", issueKey, jiraDomain, userId);
    }

    public JiraIssueAvailableTransitionsDto getTransitionsForIssue(String jiraDomain, String issueKey) {
        long userId = userContext.getUserId();
        log.info("Received request from user with ID {} to retrieve transitions for Jira issue with key={} in domain {}",
                userId, issueKey, jiraDomain);

        String issueTransitionsEndpoint = "%s/transitions".formatted(getIssueUri(issueKey));
        JiraIssueAvailableTransitionsDto responseDto =
                executeJiraRequest(jiraDomain, HttpMethod.GET, issueTransitionsEndpoint, null, JiraIssueAvailableTransitionsDto.class);

        log.info("Transitions for Jira issue with key={} in domain {} were retrieved for user with ID {}", issueKey, jiraDomain, userId);
        return responseDto;
    }

    public void setStatusForIssue(String jiraDomain, String issueKey, JiraIssueTransitionSetDto transitionDto) {
        long userId = userContext.getUserId();
        log.info("Received request from user with ID {} to set a new status for Jira issue with key={} in domain {}",
                userId, issueKey, jiraDomain);

        String issueTransitionsEndpoint = "%s/transitions".formatted(getIssueUri(issueKey));
        executeJiraRequest(jiraDomain, HttpMethod.POST, issueTransitionsEndpoint, transitionDto, Void.class);

        log.info("New status for Jira issue with key={} in domain {} was set by user with ID {}", issueKey, jiraDomain, userId);
    }

    public void createIssueLink(String jiraDomain, JiraIssueLinkCreateDto issueLinkCreateDto) {
        long userId = userContext.getUserId();
        log.info("Received request from user with ID {} to create an issue link in domain {}", userId, jiraDomain);

        executeJiraRequest(jiraDomain, HttpMethod.POST, issueLinkEndpoint, issueLinkCreateDto, Void.class);

        log.info("Issue link was created in domain {} by user with ID {}", jiraDomain, userId);
    }

    public void deleteIssueLink(String jiraDomain, String issueLinkId) {
        long userId = userContext.getUserId();
        log.info("Received request from user with ID {} to delete an issue link with ID={} in domain {}",
                userId, issueLinkId, jiraDomain);

        String issueLinkUri = getIssueLinkUri(issueLinkId);
        executeJiraRequest(jiraDomain, HttpMethod.DELETE, issueLinkUri, null, Void.class);

        log.info("Issue link with ID={} was deleted in domain {} by user with ID {}", issueLinkId, jiraDomain, userId);
    }

    public JiraIssueDto getIssue(String jiraDomain, String issueKey) {
        long userId = userContext.getUserId();
        log.info("Received request from user with ID {} to get information about issue with key={} in domain {}",
                userId, issueKey, jiraDomain);

        String issueUri = getIssueUri(issueKey);
        JiraIssueDto responseDto = executeJiraRequest(jiraDomain, HttpMethod.GET, issueUri, null, JiraIssueDto.class);

        log.info("Issue with key={} in domain {} was found for user with ID {}", issueKey, jiraDomain, userId);
        return responseDto;
    }

    public List<JiraIssueDto> getIssues(String jiraDomain) {
        long userId = userContext.getUserId();
        log.info("Received request from user with ID {} to get information about all issues in domain {}", userId, jiraDomain);

        JiraFilteredIssuesDto filteredIssuesDto = executeJiraRequest(jiraDomain, HttpMethod.GET, searchEndpoint, null, JiraFilteredIssuesDto.class);
        List<JiraIssueDto> issueDtos = filteredIssuesDto.getIssues();

        log.info("Issues in domain {} were found for user with ID {}. Total: {}", jiraDomain, userId, issueDtos.size());
        return issueDtos;
    }

    public List<JiraIssueDto> filterIssues(String jiraDomain, JiraIssueFilterDto filterDto) {
        long userId = userContext.getUserId();
        log.info("Received request from user with ID {} to get information about issues in domain {} filtered by fields: {}",
                userId, jiraDomain, filterDto);

        String issuesFilterEndpoint = "%s%s".formatted(searchEndpoint, getIssueFilterParam(filterDto));
        JiraFilteredIssuesDto filteredIssuesDto = executeJiraRequest(jiraDomain, HttpMethod.GET, issuesFilterEndpoint, null, JiraFilteredIssuesDto.class);
        List<JiraIssueDto> issueDtos = filteredIssuesDto.getIssues();

        log.info("Issues in domain {} filtered by fields: {} were found for user with ID {}. Total: {}",
                jiraDomain, filterDto, userId, issueDtos.size());
        return issueDtos;
    }

    private void validateSubtaskType(JiraIssueBaseDto createDto) {
        String issueType = createDto.getFields().getIssuetype().getName();
        JiraIssueUpdateDto.Fields.Parent parent = createDto.getFields().getParent();
        if (issueType.equals("Subtask") && parent == null) {
            throw new DataValidationException("For issue type 'Subtask' you must set parent field!");
        }
    }

    private UserJiraDto getUserJiraInfo(long userId, String jiraDomain) {
        return userServiceClient.getUserJiraInfo(userId, jiraDomain);
    }

    private <T> T executeJiraRequest(String jiraDomain, HttpMethod method, String endpoint, Object body, Class<T> responseType) {
        WebClient jiraClient = authorizeUser(jiraDomain);
        WebClient.RequestBodySpec requestSpec = jiraClient.method(method).uri(endpoint);
        if (body != null) {
            requestSpec.bodyValue(body);
        }
        return requestSpec.exchangeToMono(response -> webClientErrorHandler.handleResponse(response, responseType))
                .block();
    }

    private WebClient authorizeUser(String jiraDomain) {
        long userId = userContext.getUserId();
        UserJiraDto userJiraInfo = getUserJiraInfo(userId, jiraDomain);
        return jiraConfig.getJiraClient(jiraDomain, userJiraInfo.getJiraEmail(), userJiraInfo.getJiraToken());
    }

    private String getIssueUri(String issueKey) {
        return "%s/%s".formatted(issueEndpoint, issueKey);
    }

    private String getIssueLinkUri(String issueLinkId) {
        return "%s/%s".formatted(issueLinkEndpoint, issueLinkId);
    }

    private String getIssueFilterParam(JiraIssueFilterDto filterDto) {
        String jqlParams = issueFilters.stream()
                .map(issueFilter -> issueFilter.getUrlParam(filterDto))
                .filter(urlParam -> !urlParam.isBlank())
                .collect(Collectors.joining(" AND "));
        return jqlParams.isBlank()
                ? ""
                : "?jql=" + jqlParams;
    }
}