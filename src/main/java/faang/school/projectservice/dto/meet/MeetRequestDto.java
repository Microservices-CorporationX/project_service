
package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetRequestDto {
    @NotNull(message = "Meet id must not be null")
    Long id;
    @NotBlank(message = "Title must not be blank")
    String title;
    @NotBlank(message = "Title must not be blank")
    String description;
    @NotNull(message = "Status must not be null")
    MeetStatus status;
    @NotNull
    Long projectId;
    List<Long> userIds;
}