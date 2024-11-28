package faang.school.projectservice.service;

import faang.school.projectservice.dto.jira.webhook.JiraUpdateIssuePayload;
import faang.school.projectservice.jpa.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    public void updateTaskByJira(String issuekey, JiraUpdateIssuePayload payload) {

    }
}
