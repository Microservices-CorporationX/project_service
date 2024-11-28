package faang.school.projectservice.filter.jira.issue;

import faang.school.projectservice.dto.jira.issue.filter.JiraIssueFilterDto;
import org.springframework.stereotype.Component;

@Component
public class JiraIssueStatusFilter implements JiraIssueFilter {

    @Override
    public String getUrlParam(JiraIssueFilterDto filterDto) {
        return filterDto.getStatus() != null ? "status='%s'".formatted(filterDto.getStatus()) : "";
    }
}