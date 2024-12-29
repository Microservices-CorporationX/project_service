package faang.school.projectservice.dto.campaign;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record CreateCampaignDto (
        @NotBlank String title,
        @NotNull @Positive long projectId,
        @NotNull @Positive long createdBy
) {
}
