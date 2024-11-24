package faang.school.projectservice.service.jira;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.webclient.jira.JiraClientConfig;
import faang.school.projectservice.exception.webclient.WebClientErrorHandler;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JiraServiceTest {

    @Mock
    UserContext userContext;

    @Mock
    JiraClientConfig jiraClientConfig;

    @Mock
    UserServiceClient userServiceClient;

    @Mock
    WebClientErrorHandler errorHandler;


}