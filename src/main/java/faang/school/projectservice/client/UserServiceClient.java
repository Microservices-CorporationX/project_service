package faang.school.projectservice.client;

import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.userJira.UserJiraDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${services.user-service.host}:${services.user-service.port}${services.user-service.path}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/users/{userId}/jira/{jiraDomain}")
    UserJiraDto getUserJiraInfo(@PathVariable long userId, @PathVariable String jiraDomain);
}
