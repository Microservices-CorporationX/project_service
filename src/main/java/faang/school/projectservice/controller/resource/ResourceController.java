package faang.school.projectservice.controller.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.dto.resource.ResourceDtoStored;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.utilities.UrlUtils;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.RESOURCE)
public class ResourceController {
    private final ResourceService resourceService;
    private final UserContext userContext;

    @GetMapping(path = UrlUtils.ID)
    public ResourceDtoStored downloadResource(@PathVariable("id") @Min(1) Long resourceId) {
        return resourceService.downloadResource(resourceId);
    }

    @DeleteMapping(UrlUtils.ID)
    public long deleteResource(@PathVariable("id") @Min(1) Long resourceId) {
        return resourceService.deleteResource(resourceId, userContext.getUserId());
    }

    @PutMapping(UrlUtils.ID)
    public ResourceDto updateResource(@PathVariable("id") @Min(1) Long resourceId,
                                      @RequestBody @NotNull MultipartFile file) {
        return resourceService.updateResource(resourceId, userContext.getUserId(), file);
    }

    @PostMapping(path = UrlUtils.ID + UrlUtils.ADD)
    public ResourceDto addResource(@PathVariable("id") @Min(1) Long projectId,
                                   @RequestBody @NotNull MultipartFile file) {
        return resourceService.addResource(projectId, userContext.getUserId(), file);
    }
}
