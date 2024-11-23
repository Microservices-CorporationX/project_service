package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MeetDto {
    @Positive(message = "ID must be a positive number")
    @NotNull(message = "ID is required")
    private long id;

    @NotNull(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title should be between 3 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description should not exceed 500 characters")
    private String description;

    @Positive(message = "Project ID must be a positive number")
    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotNull(message = "Creator ID is required")
    @Positive(message = "Creator ID must be a positive number")
    private Long creatorId;
    private MeetStatus meetStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
