package ru.corporationx.projectservice.model.dto.filter;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Month;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentFilterDto {
    private Month monthPattern;
    @NotEmpty
    private List<@NonNull Long> projectIds;
}
