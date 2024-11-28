package faang.school.projectservice.filter.jira.issue;

import faang.school.projectservice.dto.jira.issue.filter.JiraIssueFilterDto;
import org.springframework.stereotype.Component;

@Component
public class JiraIssueAssigneeFilter implements JiraIssueFilter {

    @Override
    public String getUrlParam(JiraIssueFilterDto filterDto) {
        String username = filterDto.getAssigneeUsername();
        return username != null
                ? username.equals("null") ? "assignee=null" : "assignee='%s'".formatted(username)
                : "";
    }
}