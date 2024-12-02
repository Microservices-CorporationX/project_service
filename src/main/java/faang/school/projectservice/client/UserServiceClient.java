package faang.school.projectservice.client;

import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.user_jira.UserJiraDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(
        name = "user-service",
        url = "${services.user-service.host}:${services.user-service.port}${services.user-service.path}",
        configuration = FeignConfig.class
)
public interface UserServiceClient {

    @GetMapping("/user/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/users/filter")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/users/{userId}/jira/{jiraDomain}")
    UserJiraDto getUserJiraInfo(@PathVariable long userId, @PathVariable String jiraDomain);

    @PostMapping("/users")
    UserDto saveUser(@RequestBody UserDto user);
}
