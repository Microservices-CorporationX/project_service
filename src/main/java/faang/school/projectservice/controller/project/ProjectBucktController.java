package faang.school.projectservice.controller.project;

import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectBucktController {
    private final ProjectService projectService;

    @PutMapping("/{projectId}/add-cover-image")
    public String addCoverImage(@PathVariable Long projectId, @RequestBody MultipartFile coverImage) {
        return null;
    }
}
