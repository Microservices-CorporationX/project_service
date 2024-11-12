package faang.school.projectservice.filter.jirafilter;

import faang.school.projectservice.dto.jira.IssueFilterDto;
import org.springframework.stereotype.Component;

@Component
public class IssueStatusFilter implements IssueFilter {

    @Override
    public boolean isApplicable(IssueFilterDto issueFilterDto) {
        return issueFilterDto.status() != null;
    }

    @Override
    public String getJql(String projectKey, IssueFilterDto filter) {
        String jqlFormat = "project = %s AND status = %s";
        return String.format(jqlFormat, projectKey, filter.status());
    }
}
