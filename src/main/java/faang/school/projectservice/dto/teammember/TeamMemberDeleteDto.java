package faang.school.projectservice.dto.teammember;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TeamMemberDeleteDto {

    @NotNull
    private Long projectId;

    @NotNull
    private Long currentUserId;

    @NotNull
    private Long deleteUserId;
}
