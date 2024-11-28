package faang.school.projectservice.filter.jira.issue;

import faang.school.projectservice.dto.jira.issue.filter.JiraIssueFilterDto;
import org.springframework.stereotype.Component;

@Component
public class JiraIssueProjectFilter implements JiraIssueFilter {

    @Override
    public String getUrlParam(JiraIssueFilterDto filterDto) {
        return filterDto.getProjectKey() != null ? "project=%s".formatted(filterDto.getProjectKey()) : "";
    }
}
