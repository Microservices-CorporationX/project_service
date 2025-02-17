package ru.corporationx.projectservice.model.dto.filter;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.corporationx.projectservice.model.entity.ProjectStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFilterDto {
    @Size(max = 255, message = "Name pattern should not exceed 255 characters")
    private String namePattern;
    private ProjectStatus statusPattern;
}
