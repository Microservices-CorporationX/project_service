package faang.school.projectservice.client;

import faang.school.projectservice.dto.client.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${services.user-service.host}:${services.user-service.port}")
public interface UserServiceClient {

    @GetMapping("/user/exists/{userId}")
    ResponseEntity<Boolean> userExists(@PathVariable long userId);

    @GetMapping("/user/{userId}")
    ResponseEntity<UserDto> getUser(@PathVariable long userId);

    @PostMapping("/users")
    ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody List<Long> ids);
}
