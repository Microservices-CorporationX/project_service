package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import org.springframework.stereotype.Component;

@Component
public class ProjectValidator {

    public static void validateCreateSubProject(CreateSubProjectDto subProjectDto) {
        if (subProjectDto.getName() == null || subProjectDto.getName().isBlank()) {
            throw new IllegalArgumentException("Subproject name cannot be empty");
        }
        if (subProjectDto.getParentProjectId() == null) {
            throw new IllegalArgumentException("Parent project ID is required");
        }
    }

    public static void validateUpdateProject(CreateSubProjectDto subProjectDto) {
        if (subProjectDto.getName() == null || subProjectDto.getName().isBlank()) {
            throw new IllegalArgumentException("Subproject name cannot be empty");
        }
    }
}