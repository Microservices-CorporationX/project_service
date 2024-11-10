package faang.school.projectservice.service.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.filter.jirafilter.IssueFilter;
import faang.school.projectservice.mapper.IssueMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class JiraServiceImpl implements JiraService {

    private final JiraRestClient restClient;
    private final IssueMapper mapper;
    private final List<IssueFilter> issueFilter;


    @Override
    public IssueDto createIssue(String projectKey, IssueDto issueDto) {
        try {
            IssueInput issue = new IssueInputBuilder(projectKey, issueDto.issueTypeId())
                    .setSummary(issueDto.summary())
                    .setDescription(issueDto.description())
                    .setDueDate(issueDto.deadline().toDateTime())
                    .build();
            BasicIssue basicIssue = restClient.getIssueClient().createIssue(issue).claim();
            return mapper.toIssueDto((Issue) basicIssue);
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public List<IssueDto> getAllIssueByFilter(String projectKey, IssueFilterDto filter) {
        Optional<String> jql = issueFilter.stream()
                .filter(filters -> filters.isApplicable(filter))
                .map(filters -> filters.getJql(projectKey, filter))
                .findFirst();
        try {
            SearchResult result = restClient.getSearchClient().searchJql(jql.get()).claim();
            return mapper.toIssueDtos((List<Issue>) result.getIssues());
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public List<IssueDto> getAllIssues(String projectKey) {
        try {
            SearchResult result = restClient.getSearchClient()
                    .searchJql("project = " + projectKey).claim();
            return mapper.toIssueDtos((List<Issue>) result.getIssues());
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public IssueDto getIssue(String issueKey) {
        try {
            Issue issue = restClient.getIssueClient().getIssue(issueKey).claim();
            return mapper.toIssueDto(issue);
        } catch (RestClientException e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
