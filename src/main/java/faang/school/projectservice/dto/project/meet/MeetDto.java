package faang.school.projectservice.dto.project.meet;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MeetDto {
    private Long id;

    @NotNull(message = "Title must not be null")
    @NotBlank(message = "Title must not be blank")
    @Size(max = 128, message = "Title must not exceed 128 characters")
    private String title;

    private String description;

    @NotNull(message = "Start time must not be null")
    private LocalDateTime startDateTime;

    private MeetStatus status;
    private Long creatorId;

    @NotNull(message = "project id must not be null")
    private Long projectId;

    private List<Long> userIds;

    @Null
    private LocalDateTime createdAt;
    @Null
    private LocalDateTime updatedAt;
}
