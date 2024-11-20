package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateMeetDto(
        @Positive Long id,
        @NotBlank(message = "Title cannot be blank")
        @Size(max = 128, message = "The content must not exceed 128 characters")
        String title,
        @NotBlank(message = "Description cannot be blank")
        @Size(max = 512, message = "The content must not exceed 4096 characters")
        String description,
        @NotNull
        MeetStatus status,
        @NotNull
        List<Long> userIds
) {
}
