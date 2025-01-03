package faang.school.projectservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamEvent {
    private Long teamId;
    private Long authorId;
    private Long projectId;
    private LocalDateTime localDateTime;
}
