package faang.school.projectservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vacancy")
public class VacancyController {
    @GetMapping("/health")
    public String checkHealth() {
        return "Application is running!";
    }
}
