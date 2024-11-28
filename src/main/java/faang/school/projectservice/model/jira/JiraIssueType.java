package faang.school.projectservice.model.jira;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.loader.LoaderLogging;

@RequiredArgsConstructor
@Getter
public enum JiraIssueType {
    TASK(10001L),
    BUG(10004L),
    STORY(10002L),
    EPIC(10000L);

    private final Long id;
}
