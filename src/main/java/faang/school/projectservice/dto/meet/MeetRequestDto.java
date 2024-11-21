
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
    private Long id;
    @NotBlank(message = "Title must not be blank")
    private String title;
    @NotBlank(message = "Title must not be blank")
    private String description;
    @NotNull(message = "Status must not be null")
    private MeetStatus status;
    @NotNull
    private Long projectId;
    private List<Long> userIds;
}