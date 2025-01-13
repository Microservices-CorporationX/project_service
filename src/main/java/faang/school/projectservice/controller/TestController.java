package faang.school.projectservice.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class TestController {

    @GetMapping("/test")
    public String testEndpoint() {
        return "This is a test endpoint.";
    }
}
