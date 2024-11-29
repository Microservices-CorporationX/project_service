package faang.school.projectservice.validator;

import faang.school.projectservice.dto.ProjectDto;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class ProjectValidator {
    public void validate(ProjectDto dto, BiFunction<Long, String, Boolean> existsByOwnerUserIdAndName, Long ownerId) {

        if (dto.getOwnerId() == null) {
            dto.setOwnerId(ownerId);
        } else {
            throw new IllegalStateException("so far, you can create project only for yourself");
        }

        if (existsByOwnerUserIdAndName.apply(dto.getOwnerId(), dto.getName())) {
            throw new IllegalStateException(String.format(
                    "User %s already has a project with name %s",
                    dto.getOwnerId(),
                    dto.getName()));
        }
    }
}
