package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetDto {
    private Long id;
    @NotBlank(message = "Title should not be blank")
    @Size(max = 128, message = "Title's length should not be greater than 128 characters")
    private String title;
    @NotBlank(message = "Description should not be blank")
    @Size(max = 512, message = "Description's length should not be greater than 128 characters")
    private String description;
    private MeetStatus status;
    @NotNull
    private long creatorId;
    @NotNull
    private Long projectId;
    private List<Long> userIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
