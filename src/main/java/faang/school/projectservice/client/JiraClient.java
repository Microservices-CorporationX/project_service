package faang.school.projectservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "jiraClient", url = "${jira.url}", configuration = JiraFeignConfig.class)
public interface JiraClient {
}
