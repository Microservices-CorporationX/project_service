package faang.school.projectservice.dto.meet;

import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MeetDto(
        Long id,
        @NotBlank(message = "Title cannot be blank")
        @Size(max = 128, message = "The content must not exceed 128 characters")
        String title,
        @NotBlank(message = "Description cannot be blank")
        @Size(max = 512, message = "The content must not exceed 512 characters")
        String description,
        @NotNull(message = "Status cannot be null")
        MeetStatus status,
        @NotNull(message = "CreatorId cannot be null")
        @Positive(message = "CreatorId must be positive")
        Long creatorId,
        @NotNull(message = "ProjectId cannot be null")
        @Positive(message = "ProjectId must be positive")
        Long projectId,
        List<Long> userIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
