package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectsIdsDto {
    private List<
            @NotNull(message = "Project id cannot be null")
            @Positive(message = "Project id must be a positive integer"
            ) Long> ids;
}
