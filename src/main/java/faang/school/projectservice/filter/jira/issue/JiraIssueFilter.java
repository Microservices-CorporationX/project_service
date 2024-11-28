package faang.school.projectservice.filter.jira.issue;

import faang.school.projectservice.dto.jira.issue.filter.JiraIssueFilterDto;

public interface JiraIssueFilter {

    String getUrlParam(JiraIssueFilterDto filterDto);
}
